import type { JSX } from 'react'
import { z } from 'zod'
import { ArrowRight } from 'lucide-react'
import type {
  CompactUserDTO,
  ListApi,
  UserProfileApi,
  UserProfileDTO,
} from '@/api'
import { useNavigate } from '@tanstack/react-router'

export interface SettingItem {
  validator?: z.ZodTypeAny
  label: string
  type:
    | 'string'
    | 'number'
    | 'boolean'
    | 'select'
    | 'checkboxList'
    | 'navigateOnly'
    | 'preferences'
  icon?: () => JSX.Element
  onClick?: (item: SettingItem, navigate: any) => void
  getValue?: (user: CompactUserDTO, userProfile: UserProfileDTO) => any
  updateValue?: (
    value: any,
    user: CompactUserDTO,
    userProfile: UserProfileDTO,
    profileAPI: UserProfileApi,
    userAPI: any
  ) => Promise<any>
  valueProperty?: string
  getOptions?: (listAPI: ListApi) => Promise<any[]>
  invalidateQueries?: string[]
}


function LabeledSetting({
  item,
  children,
}: {
  item: SettingItem
  data: any
  children: JSX.Element
}) {
  const navigate = useNavigate()
  const clickable = Boolean(item.onClick)

  return (
    <div
      onClick={() => clickable && item.onClick?.(item, navigate)}
      className={`bg-white dark:bg-gray-900 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 px-4 py-3 flex items-center space-x-4 ${
        clickable
          ? 'hover:bg-gray-100 dark:hover:bg-gray-800 cursor-pointer transition-colors'
          : ''
      }`}
    >
      {item.icon && <div className="text-gray-500">{item.icon()}</div>}

      <div className="flex-1">
        <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
          {item.label}
        </p>
        {children}
      </div>

      {clickable && <ArrowRight className="w-4 h-4 text-gray-400" />}
    </div>
  )
}


function getStringItem(item: SettingItem, data: any, key: number) {
  return (
    <LabeledSetting key={key} item={item} data={data}>
      <label className="text-sm text-gray-600 dark:text-gray-300">
        {data[item.valueProperty || ''] ?? '—'}
      </label>
    </LabeledSetting>
  )
}

function getNumberItem(item: SettingItem, data: any, key: number) {
  return (
    <LabeledSetting key={key} item={item} data={data}>
      <label className="text-sm text-gray-600 dark:text-gray-300">
        {data[item.valueProperty || ''] ?? '—'}
      </label>
    </LabeledSetting>
  )
}

function getBooleanItem(item: SettingItem, data: any, key: number) {
  return (
    <LabeledSetting key={key} item={item} data={data}>
      <label className="text-sm text-gray-600 dark:text-gray-300">
        {data[item.valueProperty || ''] ? 'Ja' : 'Nee'}
      </label>
    </LabeledSetting>
  )
}

function getSelectItem(item: SettingItem, data: any, key: number) {
  return (
    <LabeledSetting key={key} item={item} data={data}>
      <label className="text-sm text-gray-600 dark:text-gray-300">
        {data[item.valueProperty || '']?.name ?? 'Geen'}
      </label>
    </LabeledSetting>
  )
}

function getCheckboxListItem(item: SettingItem, data: any, key: number) {
  return (
    <LabeledSetting key={key} item={item} data={data}>
      <label className="text-sm text-gray-600 dark:text-gray-300">
        {data[item.valueProperty || '']
          ?.map((v: any) => v.name)
          .join(', ') || '—'}
      </label>
    </LabeledSetting>
  )
}

function getNavigateOnlyItem(item: SettingItem, data: any, key: number) {
  return (
    <LabeledSetting key={key} item={item} data={data}>
      <label className="text-sm text-gray-600 dark:text-gray-300"></label>
    </LabeledSetting>
  )
}

function getPreferencesItem(item: SettingItem, data: any, key: number) {
  return (
    <LabeledSetting
      key={key}
      item={item}
      data={data}
    >
             <label className="
                block 
                text-sm 
                font-meduim 
                text-gray-700 
                mb-0.5"
            >
                {data[item.valueProperty || ""]?.map((v: any) => v.name).join(", ")}
            </label>
        </LabeledSetting>
    );
}


function getSchemaItem(item: SettingItem, data: any, key: number) {
  switch (item.type) {
    case 'string':
      return getStringItem(item, data, key)
    case 'number':
      return getNumberItem(item, data, key)
    case 'boolean':
      return getBooleanItem(item, data, key)
    case 'select':
      return getSelectItem(item, data, key)
    case 'checkboxList':
      return getCheckboxListItem(item, data, key)
    case 'navigateOnly':
      return getNavigateOnlyItem(item, data, key)
    case 'preferences':
      return getPreferencesItem(item, data, key)
    default:
      return <div key={key}>Unsupported type: {item.type}</div>
  }
}


export function MobileSettings({
  data,
  schema,
}: {
  data: any
  schema: SettingItem[]
}) {
  return (
    <div className="bg-background min-h-screen py-4 px-2 space-y-3">
      {schema.map((item, key) => getSchemaItem(item, data, key))}
    </div>
  )
}
