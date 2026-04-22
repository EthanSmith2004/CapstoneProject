import { ListApi, UserProfileApi } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { getItemByValueProperty } from '@/data/user-profile-schema';
import { SettingsEditor } from '@/components/mobile/SettingsEditor';
import { createFileRoute, useParams } from '@tanstack/react-router'

export const Route = createFileRoute('/user/profile/edit/$item')({
    component: RouteComponent,
})

function RouteComponent() {
    const { item } = useParams({ from: '/user/profile/edit/$item' })
    const itemSchema = getItemByValueProperty(item);
    const { getApiClient } = useAuth();
    // TODO replace with real userAPI when available
    const userAPI = {
        updateUserPassword: (data: { newPassword: string }) => { 
            console.log("Pretending to update password to", data.newPassword); 
            return Promise.resolve(); 
        },
        updateUserEmail: (data: { newEmail: string }) => { 
            console.log("Pretending to update email to", data.newEmail); 
            return Promise.resolve(); 
        }
    };
    const profileAPI = getApiClient(UserProfileApi);
    const listAPI = getApiClient(ListApi);

    return (
        <div className="p-4">
            <h1 className='font-bold'>
                {itemSchema?.label}
            </h1>
            <p className='
                mb-4 
                text-sm 
                text-gray-600'
            >
                Hier kan jy jou {itemSchema?.label.toLowerCase()} verander.
            </p>
            <SettingsEditor 
                item={itemSchema} 
                profileAPI={profileAPI} 
                userAPI={userAPI} 
                listAPI={listAPI}
            />
        </div>);
}
