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

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
		meetingService.add(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
	}

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
			return new ResponseEntity<>(
					"The participant " + participant.getLogin() + " created and added to meeting " + id,
					HttpStatus.CREATED);
		}
		meetingService.addParticipantToMeeting(meeting, participant);
		return new ResponseEntity<>("The participant " + participant.getLogin() + " added to meeting " + id,
				HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("Meeting do not exist. ", HttpStatus.NOT_FOUND);
		}
//		Collection<Participant> participants = meetingService.getMeetingParticipants(meeting);
		Collection<Participant> participants = meeting.getParticipants();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("Unable to delete. A meeting with id " + id + " not exist.",
					HttpStatus.NOT_FOUND);
		}
		meetingService.delete(meeting);
		return new ResponseEntity<Meeting>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Meeting incomingMeeting) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("Unable to update. A meeting with id " + id + " not exist.",
					HttpStatus.NOT_FOUND);
		}
		meeting.setTitle(incomingMeeting.getTitle());
		meeting.setDescription(incomingMeeting.getDescription());
		meeting.setDate(incomingMeeting.getDate());
		meetingService.update(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long id,
			@PathVariable("login") String login) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("Meeting do not exist. ", HttpStatus.NOT_FOUND);
		}
		ParticipantService participantService = new ParticipantService();
		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity<>("The participant " + login + " do not exist.", HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meeting.getParticipants();
		for (Participant p : participants) {
			if (p.getLogin().equals(login)) {
				meetingService.removeParticipantFromMeeting(meeting, participant);
				return new ResponseEntity<>("The participant " + login +
						" removed from meeting " + id, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>("The participant " + login + 
				" was not added to meeting " + id + " before.",
				HttpStatus.OK);
	}
}
