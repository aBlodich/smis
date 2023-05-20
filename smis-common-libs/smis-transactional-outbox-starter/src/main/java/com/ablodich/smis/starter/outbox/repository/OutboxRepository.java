package com.ablodich.smis.starter.outbox.repository;

import com.ablodich.smis.starter.outbox.config.OutboxProperties;
import com.ablodich.smis.starter.outbox.model.Outbox;
import com.ablodich.smis.starter.outbox.model.OutboxStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OutboxRepository {
    private static final int DEFAULT_MAX_TRY_COUNT = 10;
    private static final String INSERT_OUTBOX_QUERY = "insert into outbox(id, message_key, topic, payload, status, created_at, try_count) values(:id, :messageKey, :topic, :payload, :status, :createdAt, :tryCount)";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final OutboxProperties outboxProperties;
    private final int maxTryCount;

    public OutboxRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate, final OutboxProperties outboxProperties) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.outboxProperties = outboxProperties;

        if (outboxProperties.getMaxTryCount() == null || outboxProperties.getMaxTryCount() == 0) {
            this.maxTryCount = DEFAULT_MAX_TRY_COUNT;
        } else {
            this.maxTryCount = outboxProperties.getMaxTryCount();
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveOutbox(Outbox outbox) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", outbox.getId());
        parameters.put("messageKey", outbox.getMessageKey());
        parameters.put("topic", outbox.getTopic());
        parameters.put("payload", outbox.getPayload());
        parameters.put("status", outbox.getStatus().name());
        parameters.put("createdAt", outbox.getCreatedAt());
        parameters.put("tryCount", 0);
        namedParameterJdbcTemplate.update(INSERT_OUTBOX_QUERY, parameters);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Outbox> findAllUnprocessedOutboxes() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("maxTryCount", maxTryCount);
        return namedParameterJdbcTemplate.query("select * from outbox where try_count <= :maxTryCount order by created_at asc", parameters,
                                                this::mapResultSetToOutbox);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public int setSendErrorForMessage(final UUID messageId, final String errorDescription) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", messageId);
        parameters.put("status", OutboxStatus.SENT_ERROR.name());
        parameters.put("errorDescription", errorDescription);
        parameters.put("lastTryAt", LocalDateTime.now(ZoneId.of("UTC")));
        return namedParameterJdbcTemplate.update("update outbox set status = :status, try_count = try_count + 1, last_try_at = :lastTryAt, error_description = :errorDescription where id = :id",
                                                 parameters);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public int removeByIds(final List<UUID> ids) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        return namedParameterJdbcTemplate.update("delete from outbox where id in (:ids)", parameters);
    }

    protected Outbox mapResultSetToOutbox(ResultSet rs, int rowNum) throws SQLException {
        Outbox outbox = new Outbox();
        outbox.setId(rs.getObject("id", UUID.class));
        outbox.setPayload(rs.getString("payload"));
        outbox.setStatus(OutboxStatus.valueOf(rs.getString("status")));
        outbox.setTopic(rs.getString("topic"));
        outbox.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        outbox.setMessageKey(rs.getString("message_key"));
        outbox.setLastTryAt(rs.getObject("last_try_at", LocalDateTime.class));
        outbox.setTryCount(rs.getInt("try_count"));
        outbox.setErrorDescription(rs.getString("error_description"));
        return outbox;
    }
}
