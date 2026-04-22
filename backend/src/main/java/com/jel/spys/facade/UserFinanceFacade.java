package com.jel.spys.facade;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.model.AccountDTO;
import com.jel.spys.model.TransactionDTO;
import com.jel.spys.service.FinanceService;
import com.jel.spys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFinanceFacade {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private UserService userService;

    /**
     * Get current user's account information
     * Facade method that automatically injects current user for
     * FinanceService.getUserAccount(UserEntity user)
     */
    public AccountDTO getCurrentUserAccount() {
        UserEntity currentUser = userService.getCurrentUser();
        return financeService.getUserAccount(currentUser);
    }

    /**
     * Get paginated transactions for current user
     */
    public List<TransactionDTO> getTransactionsPaginated(int page, int size) {
        UserEntity currentUser = userService.getCurrentUser();
        return financeService.getUserTransactionsPaginated(currentUser, page, size);
    }
}
