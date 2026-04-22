import { Link, useLocation } from '@tanstack/react-router'

export interface TabItem {
  name: string;
  href: string;
  current?: boolean;
}

interface TabBarProps {
  tabs: TabItem[];
  className?: string;
}

export function TabBar({ tabs, className = '' }: TabBarProps) {
  const location = useLocation()
  
  // Mark the current tab based on current location
  const navigationWithCurrent = tabs.map(tab => ({
    ...tab,
    current: location.pathname === tab.href
  }));

  return (
    <div className={`border-b border-gray-200 ${className}`}>
      <nav className="flex space-x-8" aria-label="Navigation tabs">
        {navigationWithCurrent.map((tab) => (
          <Link
            key={tab.name}
            to={tab.href}
            className={`whitespace-nowrap pb-4 px-1 border-b-2 font-medium text-sm ${
              tab.current
                ? 'border-orange-500 text-orange-600'
                : 'border-transparent text-gray-600 hover:text-gray-700 hover:border-gray-300'
            }`}
            aria-current={tab.current ? 'page' : undefined}
          >
            {tab.name}
          </Link>
        ))}
      </nav>
    </div>
  );
}