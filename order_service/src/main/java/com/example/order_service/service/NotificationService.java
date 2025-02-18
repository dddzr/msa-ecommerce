package com.example.order_service.service;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, String> eventCache = new ConcurrentHashMap<>(); // 휘발성 이벤트 캐시 (key: emitterId, value: data)
    // TODO: 추후 비휘발성 메모리 필요 emitterRepository (redis 또는 DB 또는 파일시스템)

    // 사용자별 SSE 연결 생성
    public SseEmitter createEmitter(int userId, String lastEventId) {
        // emitterId 생성: userId + 현재 시간
        String emitterId = generateEmitterId(userId);

        SseEmitter emitter = new SseEmitter(60_000L); // 1분 타임아웃
        emitters.put(emitterId, emitter);

        // 연결이 완료되거나, 에러 발생 시 emitter 제거
        emitter.onCompletion(() -> emitters.remove(emitterId));
        emitter.onTimeout(() -> emitters.remove(emitterId));
        emitter.onError(e -> emitters.remove(emitterId));

        // 유실된 데이터 전송
        if (lastEventId != null && !lastEventId.isEmpty()) {
            sendMissedEvents(userId, lastEventId);
        }

        return emitter;
    }

    // 특정 사용자에게 알림 전송
    public void notifyUser(int userId, String message) {
        String prefix = userId + "-";
        emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .forEach(entry -> {
                    eventCache.put(entry.getKey(), message);
                    sendData(entry.getKey(), entry.getValue(), message);
            });

    }
    
     // 유실된 데이터 전송
    private void sendMissedEvents(int userId, String lastEventId) {
        String prefix = userId + "-";

        // 캐시에서 유실된 데이터 찾기
        Map<String, String> missedEvents = eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix) && entry.getKey().compareTo(lastEventId) > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        missedEvents.forEach((emitterId, data) -> {
            SseEmitter emitter = emitters.get(emitterId);
            if (emitter != null) {
                sendData(emitterId, emitter, data);
            }
        });
    }

    // 데이터 전송 공통
    private void sendData(String emitterId, SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("notification")
                    .data(data));
        } catch (IOException e) {
            emitters.remove(emitterId);
        }
    }

    // emitterId 생성
    private String generateEmitterId(int userId) {
        return userId + "-" + Instant.now().toEpochMilli();
    }
}
