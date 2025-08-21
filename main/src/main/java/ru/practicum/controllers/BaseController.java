package ru.practicum.controllers;

import java.time.format.DateTimeFormatter;

public class BaseController {
    protected final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
