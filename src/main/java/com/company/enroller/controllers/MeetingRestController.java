package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

import io.jsonwebtoken.lang.Objects;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

//	@RequestMapping(value = "/{title}", method = RequestMethod.GET)
//	public ResponseEntity<?> getMeetingT(@PathVariable("title") String title) {
//		Meeting meeting = meetingService.findByTitle(title); 
//		if (meeting == null) {
//			return new ResponseEntity(HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
//	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
		meetingService.add(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
	}
	
//	grzech

//	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
//	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") Long id,
//			@RequestBody Participant participant) {
//		Meeting meeting = meetingService.findById(id);
////		ParticipantService participantService = new ParticipantService();
//		if (meeting == null) {
//			return new ResponseEntity("Meeting do not exists. ", HttpStatus.NOT_FOUND);
//
//		}
//		Participant checkedParticipant = participantService.findByLogin(participant.getLogin());
//		if (checkedParticipant == null) {
//			participantService.addParticipant(participant);
//			meeting.addParticipant(participant);
//			meetingService.updateMeeting(meeting);
//			return new ResponseEntity<>("Participant added " + meeting + " and participant created: " + participant,
//					HttpStatus.CREATED);
//		}
//		meeting.addParticipant(checkedParticipant);
//		meetingService.updateMeeting(meeting);
//		return new ResponseEntity<>("Participant " + checkedParticipant + " added to " + meeting, HttpStatus.CREATED);
//	}

//	moje
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id,
			@RequestBody Participant participant) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("Meeting do not exist. ", HttpStatus.NOT_FOUND);
		}
		ParticipantService participantService = new ParticipantService();
		Participant foundParticipant = participantService.findByLogin(participant.getLogin());
		if (foundParticipant == null) {
			participantService.add(participant);
			meetingService.addParticipantToMeeting(meeting, participant);
			return new ResponseEntity<>("The participant: " + participant.getLogin() + 
					" created and added to meeting: " + id,
					HttpStatus.CREATED);
		}
		meetingService.addParticipantToMeeting(meeting, participant);
		return new ResponseEntity<>("The participant: " + participant.getLogin() + 
				" added to meeting: " + id,
				HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("Meeting do not exist. ", HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meetingService.getMeetingParticipants(id);
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

//	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//	public ResponseEntity<?> deleteParticipant(@PathVariable("id") String login) {
//		Participant participant = meetingService.findByLogin(login);
//		if (participant == null) {
//			return new ResponseEntity(
//					"Unable to delete. A participant with login " + login + " not exist.",
//					HttpStatus.NOT_FOUND);
//		}
//		meetingService.delete(participant);
//		return new ResponseEntity<Participant>(HttpStatus.NO_CONTENT);
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//	public ResponseEntity<?> update(@PathVariable("id") String login, @RequestBody Participant incomingPartipant) {
//		Participant participant = meetingService.findByLogin(login);
//		if (participant == null) {
//			return new ResponseEntity(HttpStatus.NOT_FOUND);
//		}
//		participant.setPassword(incomingPartipant.getPassword());
//		meetingService.update(participant);
//		return new ResponseEntity<Participant>(participant, HttpStatus.OK);
//	}
}
