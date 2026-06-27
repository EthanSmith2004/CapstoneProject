import { type UserProfileDTO, UserProfileApi} from "@/api";
import { LoadScreen } from "@/components/general/LoadScreen";
import { useQuery } from "@tanstack/react-query";
import { createContext, useContext, useEffect } from "react";
import { useAuth } from "./AuthContext";
import { useNavigate } from "@tanstack/react-router";

interface MobileUserContextType {
  userProfile: UserProfileDTO;
}

const MobileUserContext = createContext<MobileUserContextType | undefined>(undefined);

export function MobileUserProvider({ children }: { children: React.ReactNode; }) {
    const {getApiClient} = useAuth();
    const userProfileAPI = getApiClient(UserProfileApi);
    const navigate = useNavigate();
    
    const {data: userProfile, isLoading: userProfileLoading, error: profileError} = useQuery({
        queryKey: ['userProfile'],
        queryFn: async () => (await userProfileAPI.getUserProfile()).data,
        retry: (failureCount, error) => {
            if ((error as any).status === 404) {
                return false; // Do not retry if 404
            }
            return failureCount < 4; // Retry up to 2 times for other errors
        }
    });
    
    useEffect(() => {
        if (profileError && (profileError as any).status === 404) {
            // Use replace instead of navigate to avoid navigation stack issues
            navigate({ to: '/user/profile/new', replace: true });
        }
    }, [profileError, navigate]);
    
    if (userProfileLoading) {
        return <LoadScreen message="Loading User Profile..."/>;
    }

    if (profileError && (profileError as any).status === 404) {
        // Return null while navigation is happening to avoid rendering context consumers
        return null;
    }

    if (profileError) {
        return <div className="text-red-500">Error loading user profile: {profileError.message}</div>;
    }
    if (!userProfile) {
        return <div className="text-red-500">User profile not found.</div>;
    }

    const value: MobileUserContextType = {
        userProfile: userProfile,
    };

    return (
        <MobileUserContext.Provider value={value}>
        {children}
        </MobileUserContext.Provider>
    );
}

export function useMobileUser() {
  const context = useContext(MobileUserContext);
  if (!context) {
    throw new Error('useMobileUser must be used within a MobileUserProvider');
  }
  return context;
}
