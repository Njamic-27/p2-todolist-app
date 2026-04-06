package todolist.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Permisos insuficientes")
public class PermisosInsuficientesException extends RuntimeException {
}

