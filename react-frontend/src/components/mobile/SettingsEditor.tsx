import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Checkbox } from '@/components/ui/checkbox';
import { SuccessOverlay } from '@/components/ui/successOverlay';
import { FailureOverlay } from '@/components/ui/failureOverlay';
import { useNavigate } from '@tanstack/react-router';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { SettingItem } from './MobileSettings';
import type { UserProfileApi, ListApi, CompactUserDTO } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { LoadScreen } from '../general/LoadScreen';

interface SettingsEditorProps {
  item?: SettingItem;
  profileAPI: UserProfileApi;
  userAPI: any;
  listAPI: ListApi;
}

// Preference keys for localStorage
const PREFERENCE_KEYS = {
  hideAllergyDishes: 'pref_hideAllergyDishes',
  hideDislikedDishes: 'pref_hideDislikedDishes',
  sortByLikedDishes: 'pref_sortByLikedDishes',
  favTabDefault: 'pref_favTabDefault',
};

function getStoredPreferences() {
  return {
    hideAllergyDishes: localStorage.getItem(PREFERENCE_KEYS.hideAllergyDishes) === 'true',
    hideDislikedDishes: localStorage.getItem(PREFERENCE_KEYS.hideDislikedDishes) === 'true',
    sortByLikedDishes: localStorage.getItem(PREFERENCE_KEYS.sortByLikedDishes) === 'true',
    favTabDefault: localStorage.getItem(PREFERENCE_KEYS.favTabDefault) === 'true',
  };
}

function setStoredPreferences(prefs: Record<string, boolean>) {
  Object.entries(prefs).forEach(([key, value]) => {
    localStorage.setItem(PREFERENCE_KEYS[key as keyof typeof PREFERENCE_KEYS], value ? 'true' : 'false');
  });
}

export function SettingsEditor({ 
  item, 
  profileAPI, 
  userAPI, 
  listAPI 
}: SettingsEditorProps) {
  const navigate = useNavigate();
  const [value, setValue] = useState<any>('');
  const [options, setOptions] = useState<any[]>([]);
  const [showSuccess, setShowSuccess] = useState(false);
  const [showFailure, setShowFailure] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const queryClient = useQueryClient();

  // Preferences state for 'preferences' type
  const [preferences, setPreferences] = useState(getStoredPreferences());

  useEffect(() => {
    if (item?.type === 'preferences') {
      setPreferences(getStoredPreferences());
    }
  }, [item]);

  const handlePreferencesChange = (key: keyof typeof PREFERENCE_KEYS, checked: boolean) => {
    setPreferences(prev => ({
      ...prev,
      [key]: checked,
    }));
  };

  const handlePreferencesSubmit = async () => {
    setIsLoading(true);
    setStoredPreferences(preferences);
    setShowSuccess(true);
    setTimeout(() => {
      setShowSuccess(false);
      navigate({ to: '/user/profile' });
    }, 2000);
    setIsLoading(false);
  };

  const {data: userProfileResponse, isLoading: isUserLoading} = useQuery({
    queryKey: ['userProfile'],
    queryFn: () => profileAPI.getUserProfile(),
  });
  const userProfile = userProfileResponse?.data;

  const { user } = useAuth();


  // Load current value and options
  useEffect(() => {
    if (item && user && userProfile) {
      // Convert User to CompactUserDTO format for compatibility
      const compactUser: CompactUserDTO = {
        email: user.email,
        firstName: userProfile.user?.firstName,
        lastName: userProfile.user?.lastName,
        id: userProfile.user?.id,
        admin: user.roles.includes('ROLE_ADMIN')
      };
      
      const currentValue = item.getValue ? item.getValue(compactUser, userProfile) : '';
      setValue(currentValue);

      // Load options for select/checkboxList types
      if ((item.type === 'select' || item.type === 'checkboxList') && item.getOptions) {
        item.getOptions(listAPI).then(setOptions).catch(console.error);
      }
    }
  }, [item, user, userProfile, listAPI]);

  // Fetch options for different types
  const { data: campusOptions } = useQuery({
    queryKey: ['campuses'],
    queryFn: () => listAPI.getCampusNames(),
    enabled: item?.valueProperty === 'campus'
  });

  const { data: residenceOptions } = useQuery({
    queryKey: ['residences'],
    queryFn: () => listAPI.getResidenceNames(),
    enabled: item?.valueProperty === 'residence'
  });

  const { data: allergyOptions } = useQuery({
    queryKey: ['allergies'],
    queryFn: () => listAPI.getAllergyNames(),
    enabled: item?.valueProperty === 'allergies'
  });
  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async (newValue: any) => {
      const compactUser: CompactUserDTO = {
        email: user?.email,
        firstName: userProfile?.user?.firstName,
        lastName: userProfile?.user?.lastName,
        id: userProfile?.user?.id,
        admin: user?.roles.includes('ROLE_ADMIN')
      };
      if (!item?.updateValue) throw new Error("No update function defined for this setting.");
      return item.updateValue(newValue, compactUser, userProfile ?? {}, profileAPI, userAPI);
    },
    onSuccess: (data) => {
      setShowSuccess(true);
      setTimeout(() => {
        setShowSuccess(false);
        navigate({ to: '/user/profile' });
      }, 2000);
      if (item?.invalidateQueries)
        queryClient.setQueryData(item?.invalidateQueries, () => data);
    },
    onError: () => {
      setShowFailure(true);
      setTimeout(() => setShowFailure(false), 3000);
      queryClient.refetchQueries({ queryKey: item?.invalidateQueries || [] });
    }
  });

  const handleSubmit = async () => {
    setIsLoading(true);
    try {
      await updateMutation.mutateAsync(value);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    navigate({ to: '/user/profile' });
  };

  if (!item) {
    return (
        <div>
          <p className="text-red-600">Instelling nie gevind nie.</p>
          <Button onClick={handleCancel} className="mt-4">
            Terug
          </Button>
        </div>
    );
  }

  const renderEditor = () => {
    switch (item.type) {
      case 'string':
        if (item.valueProperty === 'changePassword') {
          return (
            <div className="space-y-4">
              <div>
                <Input
                  id="password"
                  type="password"
                  value={value}
                  onChange={(e) => setValue(e.target.value)}
                  placeholder="Voer nuwe wagwoord in"
                />
              </div>
            </div>
          );
        }
        return (
          <div>
            <Input
              id="value"
              type="text"
              value={value}
              onChange={(e) => setValue(e.target.value)}
              placeholder={`Voer ${item.label.toLowerCase()} in`}
            />
          </div>
        );

      case 'select':
        let selectOptions = options;
        if (item.valueProperty === 'campus' && campusOptions?.data) {
          selectOptions = campusOptions.data;
        } else if (item.valueProperty === 'residence' && residenceOptions?.data) {
          selectOptions = residenceOptions.data;
        }

        return (
          <div>
            <Select value={value?.id?.toString() || ''} onValueChange={(selectedId) => {
              const selectedOption = selectOptions.find(option => option.id?.toString() === selectedId);
              setValue(selectedOption);
            }}>
              <SelectTrigger>
                <SelectValue placeholder={`Kies ${item.label.toLowerCase()}`} />
              </SelectTrigger>
              <SelectContent>
                {selectOptions.map((option: any) => (
                  <SelectItem key={option.id} value={option.id?.toString()}>
                    {option.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        );

      case 'checkboxList':
        let checkboxOptions = options;
        if (item.valueProperty === 'allergies' && allergyOptions?.data) {
          checkboxOptions = allergyOptions.data;
        }

        const selectedIds = Array.isArray(value) ? value.map((v: any) => v.id) : [];

        return (
          <div>
            <div className="space-y-2 mt-2">
              {checkboxOptions.map((option: any) => (
                <div key={option.id} className="flex items-center space-x-2">
                  <Checkbox
                    id={`option-${option.id}`}
                    checked={selectedIds.includes(option.id)}
                    onCheckedChange={(checked) => {
                      if (checked) {
                        setValue([...(Array.isArray(value) ? value : []), option]);
                      } else {
                        setValue((Array.isArray(value) ? value : []).filter((v: any) => v.id !== option.id));
                      }
                    }}
                  />
                  <Label htmlFor={`option-${option.id}`}>{option.name}</Label>
                </div>
              ))}
            </div>
          </div>
        );

      case 'preferences':
        return (
          <div>
            <div className="mb-4 flex items-center space-x-2">
              <Checkbox
                id="hideAllergyDishes"
                checked={preferences.hideAllergyDishes}
                onCheckedChange={checked => handlePreferencesChange('hideAllergyDishes', !!checked)}
              />
              <Label htmlFor="hideAllergyDishes">Versteek geregte met my allergieë</Label>
            </div>
            <div className="mb-4 flex items-center space-x-2">
              <Checkbox
                id="hideDislikedDishes"
                checked={preferences.hideDislikedDishes}
                onCheckedChange={checked => handlePreferencesChange('hideDislikedDishes', !!checked)}
              />
              <Label htmlFor="hideDislikedDishes">Versteek onaangename geregte</Label>
            </div>
            <div className="mb-4 flex items-center space-x-2">
              <Checkbox
                id="sortByLikedDishes"
                checked={preferences.sortByLikedDishes}
                onCheckedChange={checked => handlePreferencesChange('sortByLikedDishes', !!checked)}
              />
              <Label htmlFor="sortByLikedDishes">Sorteer volgens gunsteling geregte</Label>
            </div>
            <div className="mb-4 flex items-center space-x-2">
              <Checkbox
                id="favTabDefault"
                checked={preferences.favTabDefault}
                onCheckedChange={checked => handlePreferencesChange('favTabDefault', !!checked)}
              />
              <Label htmlFor="favTabDefault">Stel gunsteling-oortjie as verstek</Label>
            </div>
            <div className="flex gap-2 pt-4">
              <Button 
                onClick={handlePreferencesSubmit} 
                disabled={isLoading}
                className="flex-1 bg-orange-600 hover:bg-orange-700"
              >
                {isLoading ? 'Stoor...' : 'Stoor'}
              </Button>
              <Button 
                variant="outline" 
                onClick={handleCancel}
                className="flex-1"
              >
                Kanselleer
              </Button>
            </div>
          </div>
        );

      default:
        return (
          <div>
            <p className="text-red-600">Instelling tipe nie ondersteun nie: {item.type}</p>
          </div>
        );
    }
  };

  if (isUserLoading || !userProfile) {
    return <LoadScreen message="Laai Gebruikers Profiel..."/>;
  }

  return (
    <>
      {renderEditor()}
      
      {/* Only show these buttons if NOT editing preferences */}
      {item.type !== 'preferences' && (
        <div className="flex gap-2 pt-4">
          <Button 
            onClick={handleSubmit} 
            disabled={isLoading}
            className="flex-1 bg-orange-600 hover:bg-orange-700"
          >
            {isLoading ? 'Stoor...' : 'Stoor'}
          </Button>
          <Button 
            variant="outline" 
            onClick={handleCancel}
            className="flex-1"
          >
            Kanselleer
          </Button>
        </div>
      )}
      {showSuccess && (
        <SuccessOverlay 
          message="Instelling suksesvol opgepdateer!"
          redirectTo="/user/profile"
        />
      )}

      {showFailure && (
        <FailureOverlay 
          message="Fout tydens opdatering. Probeer asseblief weer."
          onClose={() => setShowFailure(false)}
        />
      )}
    </>
  );
}