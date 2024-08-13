package org.g9project4.board.exceiptions;

import org.g9project4.global.exceptions.script.AlertBackException;
import org.springframework.http.HttpStatus;

public class BoardNotfoundException extends AlertBackException {
    public BoardNotfoundException() {
        super("NotFound.board", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}
