package ru.practicum.services;

import ru.practicum.entities.Request;
import ru.practicum.entities.enums.RequestStateAction;

import java.util.List;

public interface RequestService {
    Request saveRequest(Request request);

    List<Request> getRequestsByUserId(Long userId);

    Request getRequestById(Long userId, Long requestId);

    Request cancel(Request request);

    List<Request> getRequestsByUserIdAndEventId(Long eventId, Long userId);

    List<List<Request>> updateRequests(Long userId, Long eventId, List<Long> ids, RequestStateAction state);
}
