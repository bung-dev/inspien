package com.inspien.receiver.jdbc;

public record BatchResult(
        int totalCount,
        int successCount,
        int failCount
) {
    public static BatchResult from(int[] result, int requestedRows) {
        if (result == null) {
            return new BatchResult(requestedRows, 0, requestedRows);
        }

        int success = 0;
        for (int v : result) {
            if (v >= 0 || v == -2) {
                success++;
            }
        }

        return new BatchResult(requestedRows, success, requestedRows - success);
    }
}
