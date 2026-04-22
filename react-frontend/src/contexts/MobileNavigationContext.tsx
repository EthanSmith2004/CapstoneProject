import React, { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';

interface HeaderContextType {
  title: string;
  setTitle: (title: string) => void;
}

const HeaderContext = createContext<HeaderContextType | undefined>(undefined);

export const useMobileNavigation = () => {
  const context = useContext(HeaderContext);
  if (!context) {
    throw new Error('useHeader must be used within a HeaderProvider');
  }
  return context;
};

interface HeaderProviderProps {
  children: ReactNode;
}

export const MobileNavigationProvider: React.FC<HeaderProviderProps> = ({ children }) => {
  const [title, setTitle] = useState('Title');

  return (
    <HeaderContext.Provider value={{ title, setTitle }}>
      {children}
    </HeaderContext.Provider>
  );
};
