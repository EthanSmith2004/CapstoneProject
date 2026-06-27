import { UserAccountApi } from '@/api';
import { useAuth } from '@/contexts/AuthContext'
import { useMobileNavigation } from '@/contexts/MobileNavigationContext';
import { formatDate } from '@/lib/utils';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router'
import { useEffect } from 'react';
import { Wallet } from 'lucide-react';

export const Route = createFileRoute('/user/account')({
  component: RouteComponent,
})

function RouteComponent() {
  const { getApiClient } = useAuth();
  const userAccountApi = getApiClient(UserAccountApi);
  const mobileNavigation = useMobileNavigation();

  useEffect(() => {
    if (mobileNavigation.title !== 'Balance') {
      mobileNavigation.setTitle('Balance');
    }
  }, [mobileNavigation]);

  const { data: accountData, isLoading: accountDataLoading } = useQuery({
    queryKey: ['userAccountData'],
    queryFn: async () => {
      const response = await userAccountApi.getAccount();
      return response.data;
    },
  });

  const { data: accountTransactions, isLoading: accountTransactionsLoading } = useQuery({
    queryKey: ['userAccountTransactions'],
    queryFn: async () => {
      const response = await userAccountApi.getTransactionsPaginated(0, 1000);
      return response.data;
    },
  });

  if (accountDataLoading) {
    return <div>Loading account data...</div>;
  }

  return (
    <div className="p-4">
      {/* Balance Card */}
      <div className="bg-white shadow-md rounded-lg p-4 flex items-center justify-between mb-6 border border-green-100">
        <div className="flex items-center gap-3">
          <Wallet className="text-green-600 w-6 h-6" />
          <h2 className="text-lg font-semibold text-gray-700">Available Balance</h2>
        </div>
        <span className="text-2xl font-bold text-green-600">
          R{accountData?.currentBalance?.toFixed(2)}
        </span>
      </div>

      {/* Transaction History */}
      <div>
        <h3 className="text-xl font-semibold mb-3 text-gray-800">Transaction History</h3>
        {accountTransactionsLoading ? (
          <p>Loading transactions...</p>
        ) : accountTransactions && accountTransactions.length > 0 ? (
          <ul className="flex flex-col gap-3">
            {accountTransactions.map((transaction) => {
              const isCredit = (transaction.credit ?? 0) > 0;
              const amount = isCredit ? (transaction.credit ?? 0) : (transaction.debit ?? 0);

              return (
                <li
                  key={transaction.id}
                  className="bg-white shadow-sm border border-gray-200 rounded-lg p-3"
                >
                  <div className="flex justify-between items-center mb-1">
                    <span className="text-sm text-gray-600">
                      {formatDate(transaction.transactionDate)}
                    </span>
                    <span
                      className={`font-semibold ${
                        isCredit ? 'text-green-600' : 'text-red-500'
                      }`}
                    >
                      {isCredit ? '+' : '-'}R{Math.abs(amount).toFixed(2)}
                    </span>
                  </div>
                  <p className="text-sm text-gray-700 whitespace-pre">{transaction.description}</p>
                  <p className="text-xs text-gray-500 mt-1">
                    Balance after transaction: R{transaction.runningBalance?.toFixed(2)}
                  </p>
                </li>
              );
            })}
          </ul>
        ) : (
          <div className="bg-gray-50 border border-gray-200 rounded-md p-4 text-center text-gray-500">
            No transactions found.
          </div>
        )}
      </div>
    </div>
  );
}

