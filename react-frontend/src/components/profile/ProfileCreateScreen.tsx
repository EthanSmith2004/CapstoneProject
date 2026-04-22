import { UserProfileApi, type CreateUserProfileRequest } from "@/api";
import { useAuth } from "@/contexts/AuthContext";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { useEffect } from "react";
import { ProfileForm } from "./ProfileForm";

export function ProfileCreateScreen() {
  const { getApiClient } = useAuth()
  const profileAPI = getApiClient(UserProfileApi)
  const navigate = useNavigate()

  // Check if user already has a profile
  const userProfileQuery = useQuery({
    queryKey: ['user-profile'],
    queryFn: () => profileAPI.getUserProfile()
  });

  useEffect(() => {
        if (userProfileQuery.data && userProfileQuery.data.data) {
            navigate({ to: "/user/menu" });
        }
    }, [userProfileQuery, navigate]
  );

  const { mutate: createProfile, isPending: isLoading } = useMutation({
    mutationKey: ['create-profile'],
    mutationFn: (newProfile: CreateUserProfileRequest) => {
      return profileAPI.createUserProfile(newProfile)
    },
    onSuccess: (data) => {
      console.log("Profile created successfully:", data)
      navigate({ to: "/user/menu" })
    },
    onError: (error) => {
      console.error("Failed to create profile:", error)
    }
  })

  return (
    <ProfileForm submit={createProfile} isLoading={isLoading} />
  )
}