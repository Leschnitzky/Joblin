const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

/**
 * Triggers when a user gets matched.
 *
 * Followers add a flag to `/followers/{followedUid}/{followerUid}`.
 * Users save their device notification tokens to `/Users/{userEmail}/Token/{notificationToken}`.
 */
exports.sendFollowerNotificationCand = functions.firestore.document('/Recruiters/{recruiterEmail}/Matches/{candidateEmail}')
    .onCreate((snap, context) => {

      const recrEmail = context.params.recruiterEmail;
      const candEmail = snap.data().email;

      console.log(`CAND_LOG : CAND : ${candEmail}, RECR: ${recrEmail}`)
      // If un-follow we exit the function.


      const db = admin.firestore();

      var tokens = []; 
      return Promise.all([db.collection("Users").doc(recrEmail).collection("Tokens").get(),
      		 db.collection('Recruiters').doc(recrEmail).get()]
      	).then(values => {
      		console.log(values);
        values[0].forEach(doc => {
            var newelement = doc.data()['token'];
            console.log("ADDING ELEMENT "+ newelement);
            tokens.push(newelement);
        });
        name = values[1].data()['name'];
      console.log(tokens);


      // Notification details.
      const payload = {
      	 // notification: {
        //   title: 'You have a new match!',
        //   body: `${candEmail} is now matched with you.`,
        // },
        data: {
          title: 'recruiter match',
          body: `${name} is now matched with you!`,
        }
      };

      // Send notifications to all tokens.

      return admin.messaging().sendToDevice(tokens, payload);
    }).catch(function(error) {
    	console.log("Error sending message:", error);
    });
});

exports.sendFollowerNotificationRecr = functions.firestore.document('/Candidates/{candidateEmail}/Matches/{recruiterEmail}')
    .onCreate((snap, context) => {

      const candEmail = context.params.candidateEmail;
      const recrEmail = snap.data()['email'];

      console.log(recrEmail)

      console.log(`CAND_LOG : CAND : ${candEmail}, RECR: ${recrEmail}`)
      // If un-follow we exit the function.


      const db = admin.firestore();
      var name = "";

      var tokens = []; 
      console.log(`/Users/${recrEmail}/Tokens`);
      return Promise.all([db.collection("Users").doc(recrEmail).collection("Tokens").get(),
      		 db.collection('Candidates').doc(candEmail).get()]
      	).then(values => {
      		console.log(values);
        values[0].forEach(doc => {
            var newelement = doc.data()['token'];
            console.log("ADDING ELEMENT "+ newelement);
            tokens.push(newelement);
        });
        name = values[1].data()['name'];
      console.log(tokens);


      // Notification details.
      const payload = {
      	 // notification: {
        //   title: 'You have a new match!',
        //   body: `${candEmail} is now matched with you.`,
        // },
        data: {
          title: 'candidate match',
          body: `${name} is now matched with you!`,
        }
      };

      // Send notifications to all tokens.

      return admin.messaging().sendToDevice(tokens, payload);
    }).catch(function(error) {
    	console.log("Error sending message:", error);
    });
});


exports.sendMessageToUser = functions.firestore.document('/Chats/{addedChat}')
    .onCreate((snap, context) => {

      const recieverMail = snap.data()['receiver'];
      const senderMail = snap.data()['sender'];

      console.log(recieverMail)

      console.log(`REC_MAIL :${recieverMail}, SENDER_MAIL: ${senderMail}`)
      // If un-follow we exit the function.


      const db = admin.firestore();
      var name = "";

      var tokens = []; 
      console.log(`/Users/${recieverMail}/Tokens`);
      return Promise.all([db.collection("Users").doc(recieverMail).collection("Tokens").get(),
      		 db.collection('Candidates').doc(senderMail).get(),
      		 db.collection('Recruiters').doc(senderMail).get()
      		 ]
      	).then(values => {
      		console.log(values);
        values[0].forEach(doc => {
            var newelement = doc.data()['token'];
            console.log("ADDING ELEMENT "+ newelement);
            tokens.push(newelement);
        });
        if(values[1].exists) {
        	name = values[1].data()['name'];
        } else if(values[2].exists) {
        	name = values[2].data()['name'];
        }
      console.log(tokens);


      // Notification details.
      const payload = {
      	 // notification: {
        //   title: 'You have a new match!',
        //   body: `${candEmail} is now matched with you.`,
        // },
        data: {
          title: 'You have a new message!',
          body: `${name} sent your a message`,
        }
      };

      // Send notifications to all tokens.

      return admin.messaging().sendToDevice(tokens, payload);
    }).catch(function(error) {
    	console.log("Error sending message:", error);
    });
});