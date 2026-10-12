package io.github.CMPUT301F26stderr.app.di

import io.github.CMPUT301F26stderr.app.data.account.FirestoreAccountRepository
import io.github.CMPUT301F26stderr.app.data.booking.FirestoreBookingRepository
import io.github.CMPUT301F26stderr.app.data.campsite.FirestoreCampsiteRepository
import io.github.CMPUT301F26stderr.app.data.firestore.FirebaseClients
import io.github.CMPUT301F26stderr.app.data.image.FirebaseImageRepository
import io.github.CMPUT301F26stderr.app.data.lottery.FirestoreLotteryRepository
import io.github.CMPUT301F26stderr.app.data.messaging.FirestoreMessageRepository
import io.github.CMPUT301F26stderr.app.data.moderation.FirestoreModerationRepository
import io.github.CMPUT301F26stderr.app.data.notification.FirestoreNotificationRepository
import io.github.CMPUT301F26stderr.app.data.review.FirestoreReviewRepository
import io.github.CMPUT301F26stderr.app.domain.account.AccountRepository
import io.github.CMPUT301F26stderr.app.domain.booking.BookingRepository
import io.github.CMPUT301F26stderr.app.domain.campsite.CampsiteRepository
import io.github.CMPUT301F26stderr.app.domain.image.ImageRepository
import io.github.CMPUT301F26stderr.app.domain.lottery.LotteryRepository
import io.github.CMPUT301F26stderr.app.domain.messaging.MessageRepository
import io.github.CMPUT301F26stderr.app.domain.moderation.ModerationRepository
import io.github.CMPUT301F26stderr.app.domain.notification.NotificationRepository
import io.github.CMPUT301F26stderr.app.domain.review.ReviewRepository
import java.time.Clock
import kotlin.random.Random

/** Application-scoped composition root. Bootstrap/auth and controller wiring remain TODO. */
class AppContainer(
    clients: FirebaseClients,
    clock: Clock = Clock.systemUTC(),
    random: Random = Random.Default,
) {
    val accounts: AccountRepository = FirestoreAccountRepository(clients.firestore, clock)
    val campsites: CampsiteRepository = FirestoreCampsiteRepository(clients.firestore, clock)
    private val lotteryImplementation = FirestoreLotteryRepository(clients.firestore, clock, random)
    val lotteries: LotteryRepository = lotteryImplementation
    val bookings: BookingRepository = FirestoreBookingRepository(clients.firestore, clock)
    private val imageImplementation =
        FirebaseImageRepository(clients.firestore, clients.storage, clock)
    val images: ImageRepository = imageImplementation
    val reviews: ReviewRepository = FirestoreReviewRepository(clients.firestore, clock)
    val notifications: NotificationRepository =
        FirestoreNotificationRepository(clients.firestore, clock)
    val messages: MessageRepository = FirestoreMessageRepository(clients.firestore, clock)
    val moderation: ModerationRepository =
        FirestoreModerationRepository(clients.firestore, images, lotteryImplementation, clock)

    /**
     * Invoke on a controller-owned foreground schedule after Firebase/auth initialization. Failure
     * is surfaced to the caller; subsequent invocation resumes durable work.
     */
    suspend fun resumePendingWork() {
        moderation.resumeRemovals()
        lotteryImplementation.resumeDraws()
        notifications.resumeDeliveries()
        imageImplementation.resumeCleanup()
    }
}
