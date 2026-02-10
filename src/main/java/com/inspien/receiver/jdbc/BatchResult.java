package com.inspien.receiver.jdbc;

import java.sql.Statement;

public record BatchResult(
        int totalCount,
        int successCount,
        int failCount
) {
    public static BatchResult from(int[] result, int totalCount) {
        if (result == null) {
            return new BatchResult(totalCount, 0, totalCount);
        }

        int success = 0;
        for (int v : result) {
            if (v > 0 || v == Statement.SUCCESS_NO_INFO) {
                success++;
            }
        }

        return new BatchResult(totalCount, success, totalCount - success);
    }
}
