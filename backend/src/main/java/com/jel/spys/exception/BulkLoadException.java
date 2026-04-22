package com.jel.spys.exception;

import com.jel.spys.model.AdminBulkLoadRequest;
import com.jel.spys.model.AdminFinanceLoadResponse;
import lombok.Getter;

public class BulkLoadException extends RuntimeException {
    @Getter
    private final AdminFinanceLoadResponse response;
    public BulkLoadException(AdminFinanceLoadResponse message) {
        super("Bulk load Error");
        this.response = message;
    }
}
