package hk.pipgamestudio.dailybibleverse.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import hk.pipgamestudio.dailybibleverse.entity.Emails;

@Repository
public class FirestoreEmailRepository {
	private final static String COLLECTION_NAME = "emails";
	private final static String DOCUMENT_NAME = "detail";
	
	public boolean containsAddress(String emailAddress) {
		boolean result = false;
		
		List<String> addresses = findAll();
		for (String s : addresses) {
			if (s.equalsIgnoreCase(emailAddress)) {
				return true;
			}
		}
		
		return result;
	}
	
	public List<String> findAll() {
		List<String> addresses = new ArrayList<>();
		
		Emails emails = getEmails();
		if (null == emails) {
			emails = new Emails();
		}
		
		addresses = emails.getAddresses();
		if (null == addresses) {
			addresses = new ArrayList<String>();
		}

		return addresses;
	}
	
	public void save(String emailAddress) {
		Emails emails = getEmails();
		if (null == emails) {
			emails = new Emails();
		}
		
		List<String> addresses = emails.getAddresses();
		if (null == addresses) {
			addresses = new ArrayList<String>();
		}
		
		if (!addresses.contains(emailAddress)) {
			addresses.add(emailAddress);
		}
		emails.setAddresses(addresses);
		
		saveEmails(emails);
	}
	
	public void deleteById(String emailAddress) {
		Emails emails = getEmails();
		if (null == emails) {
			return;
		}
		
		List<String> addresses = emails.getAddresses();
		if (null == addresses) {
			return;
		}
		if (!addresses.contains(emailAddress)) {
			return;
		}
		
		addresses.remove(emailAddress);
		emails.setAddresses(addresses);
		
		saveEmails(emails);
	}
	
	private Emails getEmails() {
		Emails emails = null;
		
		try {
			Firestore db = FirestoreClient.getFirestore();

			DocumentReference docRef = db.collection(COLLECTION_NAME).document(DOCUMENT_NAME);
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot document = future.get();
			if (document.exists()) {
			    // Convert the document snapshot to a Java object
			    emails = document.toObject(Emails.class);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return emails;
	}
	
	private void saveEmails(Emails emails) {
		try {
			Firestore db = FirestoreClient.getFirestore();
			
			CollectionReference emailsRef = db.collection(COLLECTION_NAME);
			ApiFuture<WriteResult> future = emailsRef.document(DOCUMENT_NAME).set(emails);
			
			WriteResult result = future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
