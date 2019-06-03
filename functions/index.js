const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

/**
 * Triggers when a user gets matched.
 *
 * Followers add a flag to `/followers/{followedUid}/{followerUid}`.
 * Users save their device notification tokens to `/Users/{userEmail}/Token/{notificationToken}`.
 */
exports.sendFollowerNotificationRec = functions.database.ref('/Cand/{recruiterEmail}/Matches/{candEmail}')
    .onWrite(async (change, context) => {
      const recruiterEmail = context.params.recruiterEmail;
      const candEmail = context.params.candEmail;
      // If un-follow we exit the function.


      // Get the device's notification tokens.
      const getDeviceTokensPromise = admin.database()
          .ref(`/users/${candEmail}/Token`).once('value');

      // The snapshot to the user's tokens.
      let tokensSnapshot;

      // The array containing all the user's tokens.
      let tokens;

      const results = await Promise.all([getDeviceTokensPromise]);
      tokensSnapshot = results[0];

      // Check if there are any device tokens.
      if (!tokensSnapshot.hasChildren()) {
        return console.log('There are no notification tokens to send to.');
      }
      console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');

      // Notification details.
      const payload = {
        notification: {
          title: 'You have a new match!',
          body: `${recruiterEmail} is now following you.`,
        }
      };

      // Listing all tokens as an array.
      tokens = Object.keys(tokensSnapshot.val());
      // Send notifications to all tokens.
      const response = await admin.messaging().sendToDevice(tokens, payload);
});