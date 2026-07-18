package ewm.core.dto;

import java.util.List;

public record EventRequestStatusUpdateRequest(
        List<Long> requestIds,
        String status
) {}