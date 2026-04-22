import type { SettingItem } from '@/components/mobile/MobileSettings'

export const userProfileSchema: SettingItem[] = [
  {
    label: 'Naam',
    type: 'string',
    valueProperty: 'name'
  },
  {
    label: 'E-pos',
    type: 'string',
    valueProperty: 'email',
    onClick: (_, navigate) => {
      navigate({ to: '/user/profile/edit/email' })
    },
    getValue(user, _) {
      return user.email;
    },
    updateValue(value, _, __, ___, userAPI) {
      return userAPI.updateUser({ email: value });
    },
    invalidateQueries: ['userProfile', 'user']
  },
  {
    label: 'Wagwoord',
    type: 'string',
    valueProperty: 'changePassword',
    onClick: (_, navigate) => {
      navigate({ to: '/user/profile/edit/changePassword' })
    },
    getValue(_, __) {
      return '';
    },
    updateValue(value, _, __, ___, userAPI) {
      return userAPI.updateUserPassword({ newPassword: value });
    },
    invalidateQueries: ['user']
  },
  {
    label: 'Kampus',
    type: 'select',
    valueProperty: 'campus',
    onClick: (_, navigate) => {
      navigate({ to: '/user/profile/edit/campus' })
    },
    getValue(_, userProfile) {
      return userProfile.campus;
    },
    updateValue(value, _, __, profileAPI, ___) {
      return profileAPI.updateUserProfile({ campusId: value.id });
    },
    invalidateQueries: ['userProfile']
  },
  {
    label: 'Koshuis',
    type: 'select',
    valueProperty: 'residence',
    onClick: (_, navigate) => {
      navigate({ to: '/user/profile/edit/residence' })
    },
    getValue(_, userProfile) {
      return userProfile.residence;
    },
    updateValue(value, __, _, profileAPI, ___) {
      return profileAPI.updateUserProfile({ residenceId: value.id });
    },
    invalidateQueries: ['userProfile']
  },
  {
    label: 'Allergieë',
    type: 'checkboxList',
    valueProperty: 'allergies',
    onClick: (_, navigate) => {
      navigate({ to: '/user/profile/edit/allergies' })
    },
    getValue(_, userProfile) {
      return userProfile.allergies;
    },
    updateValue(value, _, profile, profileAPI, ___) {
      console.log("Updating allergies to", value);
      if (value === undefined || value === null || value === '') value = profile.allergies || [];
      const allergyIds = value.map((a: any) => a.id);
      return profileAPI.updateUserProfile({ allergyIds });
    },
    invalidateQueries: ['userProfile']
  },
  {
    label: 'Rekening',
    type: 'string',
    valueProperty: 'accountBalance',
    onClick: (_, navigate: any) => {
      navigate({ to: '/user/account' })
    },
  },
  {
    label: 'Voorkeure',
    type: 'preferences',
    valueProperty: 'preferences',
    onClick: (_, navigate) => {
      navigate({ to: '/user/profile/edit/preferences' })
    },
  },
  {
    label: 'Teken uit',
    type: 'navigateOnly',
    onClick: (_, navigate) => {
      navigate({ to: '/logout' })
    },
  }
]

export function getItemByValueProperty(valueProperty: string) {
  return userProfileSchema.find(item => item.valueProperty === valueProperty)
}
