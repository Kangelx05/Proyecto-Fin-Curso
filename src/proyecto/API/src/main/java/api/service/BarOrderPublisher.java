package api.service;

import api.dto.BarOrderResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manejador para emitir eventos de pedidos de barra mediante Server-Sent Events (SSE).
 * <p>
 * Mantiene una lista de suscriptores ({@link SseEmitter}) que se registran a través del
 * controlador HTTP.  Cuando se produce un nuevo pedido de barra o cambia el estado de los
 * pendientes, se envía la lista completa a todos los suscriptores.  Si un emisor produce
 * un error al enviar, se elimina automáticamente de la lista de suscriptores.
 */
@Component
public class BarOrderPublisher {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * Registra un nuevo emisor SSE.  El cliente quedará suscrito a eventos futuros de pedidos de barra.
     *
     * @return el {@link SseEmitter} registrado
     */
    public SseEmitter registerEmitter() {
        // timeout 0L significa que no caduca automáticamente
        SseEmitter emitter = new SseEmitter(0L);
        this.emitters.add(emitter);
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError((ex) -> this.emitters.remove(emitter));
        return emitter;
    }

    /**
     * Envía la lista de pedidos de barra a todos los emisores registrados.
     *
     * @param orders lista de pedidos de barra que se enviará a los clientes
     */
    public void publishOrders(List<BarOrderResponse> orders) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(orders);
            } catch (IOException ex) {
                // En caso de error, eliminar el emisor defectuoso
                emitters.remove(emitter);
            }
        }
    }
}