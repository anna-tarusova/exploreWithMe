package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entities.Event;
import ru.practicum.entities.Request;
import ru.practicum.entities.enums.EventState;
import ru.practicum.entities.enums.RequestState;
import ru.practicum.entities.enums.RequestStateAction;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Request saveRequest(Request request) {
        try {
            Event event = request.getEvent();
            if (Objects.equals(event.getUser().getId(), request.getUser().getId())) {
                throw new ConflictException("You can't request your own event");
            }

            if (event.getState() != EventState.PUBLISHED) {
                throw new ConflictException(String.format("Event with id = %d is not in the PUBLISHED state", event.getId()));
            }

            if (event.getParticipantLimit() == 0) {
                request.setState(RequestState.CONFIRMED);
            } else {
                int confirmedRequestsCount = requestRepository.countOfRequests(event.getId());
                int remainRequestsCount = event.getParticipantLimit() - confirmedRequestsCount;
                if (remainRequestsCount <= 0) {
                    throw new ConflictException("Count of requests exceeded");
                }
                request.setState(RequestState.PENDING);
            }
            return requestRepository.save(request);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь уже подал заявку на это событие");
        }
    }

    @Override
    public List<Request> getRequestsByUserId(Long userId) {
        return requestRepository.findByUserId(userId);
    }

    @Override
    public Request getRequestById(Long userId, Long requestId) {
        return requestRepository.findByUserIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));
    }

    @Override
    public Request cancel(Request request) {
        request.setState(RequestState.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getRequestsByUserIdAndEventId(Long eventId, Long userId) {
        Event event = eventRepository.findByUserIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d of user with id = %d not found", eventId, userId)));

        return event.getRequests();
    }

    @Override
    @Transactional
    public List<List<Request>> updateRequests(Long userId, Long eventId, List<Long> ids, RequestStateAction stateAction) {
        try {
            Event event = eventRepository.findByUserIdAndId(userId, eventId)
                    .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));

            if (event.getParticipantLimit() != 0) {
                int countRequest = requestRepository.countOfConfirmedRequests(eventId);
                if (countRequest >= event.getParticipantLimit()) {
                    throw new ConflictException("Participant limit exceeded");
                }
            }

            RequestState state = stateAction == RequestStateAction.CONFIRMED ? RequestState.CONFIRMED : RequestState.REJECTED;

            if (state == RequestState.CONFIRMED) {
                if (event.getParticipantLimit() != 0) {
                    int countOfOtherConfirmedRequests = requestRepository.countOfOtherConfirmedRequests(eventId, ids);
                    int remain = event.getParticipantLimit() - countOfOtherConfirmedRequests;

                    if (remain < ids.size()) {
                        throw new ConflictException(String.format("Participation limit of the event with id = %d has been exceeded", eventId));
                    }
                }
            }

            List<Request> requests = requestRepository.findAllById(ids);
            if (state == RequestState.REJECTED && requests.stream().anyMatch(r -> r.getState() == RequestState.CONFIRMED)) {
                throw new ConflictException("Confirmed requests cannot be rejected");
            }

            requests.forEach(r -> r.setState(state));
            requestRepository.saveAll(requests);

            List<Request> requestsOfUser = requestRepository.findByEventId(eventId);
            List<List<Request>> response = new ArrayList<>(2);
            response.add(requestsOfUser.stream().filter(r -> r.getState() == RequestState.CONFIRMED).toList());
            response.add(requestsOfUser.stream().filter(r -> r.getState() == RequestState.REJECTED).toList());
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Request can not be added more than one time");
        }
    }
}
