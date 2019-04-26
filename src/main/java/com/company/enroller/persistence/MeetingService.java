package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting findById(long id) {
		return (Meeting) connector.getSession().get(Meeting.class, id);
		// zeby sprawdzic czy dziala w np. przegladarke
		// http://localhost:8080/meetings/2 lub 3 itd.
	}

	public Meeting add(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
		return meeting;
	}

	public Participant addParticipantToMeeting(Meeting meeting, Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		meeting.addParticipant(participant);
		connector.getSession().merge(meeting);
		transaction.commit();
		return participant;
	}

//	niepotrzebne
//	public Collection<Participant> getMeetingParticipants(Meeting meeting) {
//		return meeting.getParticipants();
//	}

	public void delete(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}
	
	public void update(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(meeting);
		transaction.commit();
	}
	
	public void removeParticipantFromMeeting(Meeting meeting, Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		meeting.removeParticipant(participant);
		connector.getSession().merge(meeting);
		transaction.commit();
	}
}
