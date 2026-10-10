{
  description = "stderr-app development environment";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { nixpkgs, ... }:
    let
      system = "x86_64-linux";

      pkgs = import nixpkgs {
        inherit system;
        config = {
          allowUnfree = true;
          android_sdk.accept_license = true;
        };
      };

      android = pkgs.androidenv.composeAndroidPackages {
        platformVersions = [ "37.0" ];
        buildToolsVersions = [ "36.0.0" "37.0.0" ];

        includeEmulator = false;
        includeSystemImages = false;
        includeNDK = false;
        includeCmake = false;
      };

      sdk = "${android.androidsdk}/libexec/android-sdk";

      # Match the formatter version and checksum in .pre-commit-config.yaml.
      # The locked nixpkgs ktfmt package is older than the shared hook version.
      ktfmtJar = pkgs.fetchurl {
        url = "https://repo.maven.apache.org/maven2/com/facebook/ktfmt/0.64/ktfmt-0.64-with-dependencies.jar";
        hash = "sha256-Wz1Shv0t78x9yOKMId3xVsxrLYaCvc2SnOQzPnpiAfI=";
      };

      ktfmt = pkgs.writeShellApplication {
        name = "ktfmt";
        runtimeInputs = [ pkgs.jdk25 ];
        text = ''
          exec java -jar ${ktfmtJar} --kotlinlang-style "$@"
        '';
      };
    in
    {
      devShells.${system}.default = pkgs.mkShell {
        packages = [
          pkgs.jdk25
          android.androidsdk
          pkgs.bash
          pkgs.git
          pkgs.gh
          pkgs.direnv
          pkgs.actionlint
          pkgs.shellcheck
          pkgs.pre-commit
          pkgs.python3
          pkgs.go
          pkgs.gitleaks
          ktfmt
        ];

        JAVA_HOME = pkgs.jdk25.home;
        ANDROID_HOME = sdk;

        # Use the Nix-patched executable rather than the Maven binary on NixOS.
        GRADLE_OPTS =
          "-Dorg.gradle.project.android.aapt2FromMavenOverride="
          + "${sdk}/build-tools/37.0.0/aapt2";

        shellHook = ''
          export PATH="${sdk}/platform-tools:$PATH"
        '';
      };
    };
}
