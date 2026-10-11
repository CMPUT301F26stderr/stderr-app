# UI conventions

Guidance for upcoming UI implementation. The table below records preferred approaches; it does not declare dependencies installed or integrations complete. Keep detailed designs and team guidance in the Wiki.

## Dependency policy

Prefer Jetpack and Android platform components. Use official Google SDKs for Firebase, maps, and scanning. Allow a small number of established libraries when they remove substantial infrastructure work.

Jetpack does not include every Google SDK: Firebase, Google Maps, and Google Code Scanner are separate products. Official Google SDKs are not necessarily fully open source.

| Feature | Preferred approach |
| --- | --- |
| Screens, forms, dialogs, navigation | Compose, Material 3, and Jetpack Navigation |
| Featured carousel | Compose carousel or `HorizontalPager`; ranking remains application logic |
| Date/range selection | Material 3 `DatePicker` / `DateRangePicker` |
| Booked/free-night calendar | A shared Compose component initially; consider a calendar library if the design becomes substantial |
| Photo selection | Android Photo Picker through Activity Result contracts |
| Maps | Google Maps Compose; project/API setup is required |
| QR scanning | Google Code Scanner; depends on Google Play services |
| QR generation | ZXing; generate from the stable campsite identity |
| Remote image display | Coil for downloading, decoding, resizing, caching, and loading/error states |
| CSV export | A small tested encoder with correct escaping, connected to Android file creation/sharing APIs |

Coil and ZXing are the initial community-library exceptions. Check Android/Compose compatibility and licensing when selecting versions. Add application dependencies through the Gradle version catalog; development tools belong in the Nix flake.

## Components and state

- Follow the agreed prototypes and shared theme. Use standard Material components when they fit.
- Share feature components such as `CampsiteCalendar` and `CampsiteMap`, with application models and callbacks as their public API. Keep specialized library details within their integration components.
- Screen content receives state and callbacks. Routes connect ViewModels and navigation. Reusable components do not construct repositories or acquire ViewModels.
- Keep lottery eligibility, availability validation, booking, refunds, and persistence in application/repository logic. Date-picker selection must still validate every occupied night.
- Trigger writes through explicit user actions. Display confirmed success only after the repository operation succeeds.
- Keep loading, empty, failure, and in-progress states explicit. Preserve entered data after failures.

## Accessibility and previews

- Use shared colors, typography, spacing, and shapes; put user-visible text in string resources.
- Preserve system font scaling and apply the account's font/control scaling consistently. Use layouts that tolerate large text.
- Provide meaningful semantics and labels. Communicate availability and errors with more than color alone; color-blind mode uses a shared palette.
- Add themed previews with realistic fixtures, long text, and enlarged fonts. Keep interactive demos separate from production screens; do not fake successful saves or put throwing placeholders in preview paths.

## References

- [Jetpack libraries](https://developer.android.com/jetpack)
- [Date pickers](https://developer.android.com/develop/ui/compose/components/datepickers), [carousel](https://developer.android.com/develop/ui/compose/components/carousel), and [accessibility](https://developer.android.com/develop/ui/compose/accessibility)
- [Photo Picker](https://developer.android.com/training/data-storage/shared/photo-picker), [Google Code Scanner](https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner), and [Maps Compose](https://github.com/googlemaps/android-maps-compose)
- [Image loading guidance](https://developer.android.com/develop/ui/compose/graphics/images/loading), [Coil](https://github.com/coil-kt/coil), and [ZXing](https://github.com/zxing/zxing)
