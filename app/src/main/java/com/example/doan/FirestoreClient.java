package com.example.doan;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.concurrent.CompletableFuture;

public class FirestoreClient
{
    private static FirestoreClient instance;

    private final FirebaseFirestore db;

    private FirestoreClient() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirestoreClient getInstance()
    {
        if (instance == null) {
            instance = new FirestoreClient();
        }
        return instance;
    }


    public CompletableFuture<String> Write(FirestoreClass.User user) {
        CollectionReference usersRef = db.collection("Users");
        CompletableFuture<String> future = new CompletableFuture<>();

        usersRef.add(user)
                .addOnSuccessListener(documentReference -> future.complete(documentReference.getId()))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    public CompletableFuture<FirestoreClass.User> UserFetch(String id) {
        DocumentReference userRef = db.collection("Users").document(id);

        CompletableFuture<FirestoreClass.User> future = new CompletableFuture<>();

        userRef.get()
                .addOnSuccessListener(documentSnapshot ->  {
                        if (documentSnapshot.exists()) {
                            FirestoreClass.User user = documentSnapshot.toObject(FirestoreClass.User.class);
                            future.complete(user);
                        } else {
                            future.complete(null); // or handle the absence of the document as needed
                        }
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    public CompletableFuture<FirestoreClass.User> UserFetchByEmail(String email) {
        Query userQuery = db.collection("Users").whereEqualTo("email", email);
        CompletableFuture<FirestoreClass.User> future = new CompletableFuture<>();

        userQuery.get()
                .addOnSuccessListener(querySnapshot ->  {
                    // Check if the query returned any documents
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        FirestoreClass.User user = document.toObject(FirestoreClass.User.class);
                        future.complete(user);
                    } else {
                        future.complete(null); // Handle the case where no user is found
                    }
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

}


