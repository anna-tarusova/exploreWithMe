package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.practicum.entities.Event;
import ru.practicum.entities.Request;
import ru.practicum.entities.enums.RequestState;
import ru.practicum.entities.enums.RequestStateAction;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public Request saveRequest(Request request) {
        try {
            request.setState(RequestState.PENDING);
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
        request.setState(RequestState.REJECTED);
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getRequestsByUserIdAndEventId(Long eventId, Long userId) {
        return requestRepository.findAllByUserIdAndEventId(userId, eventId);
    }

    @Override
    public List<List<Request>> updateRequests(Long userId, Long eventId, List<Long> ids, RequestStateAction stateAction) {
        try {
            Event event = eventRepository.findByUserIdAndId(userId, eventId)
                    .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d not found", eventId)));
            RequestState state = stateAction == RequestStateAction.CONFIRMED ? RequestState.CONFIRMED : RequestState.REJECTED;

            if (state == RequestState.CONFIRMED) {
                int countOfOtherConfirmedRequests = requestRepository.countOfOtherConfirmedRequests(eventId, ids);
                int remain = event.getParticipantLimit() - countOfOtherConfirmedRequests;

                if (remain < ids.size()) {
                    throw new ConflictException(String.format("Participation limit of the event with id = %d has been exceeded", eventId));
                }
            }

            List<Request> requests = requestRepository.findAllById(ids);
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
