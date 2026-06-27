// import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { 
    Shield, 
    Users, 
    LogOut, 
    Menu, 
    DollarSign,
    Settings,
    NotebookText,
    MessageSquareMore,
    ArrowUpDown,
    TruckIcon
} from 'lucide-react'
import { Outlet, useLocation, useNavigate } from '@tanstack/react-router'
// // import { AdminApi } from '../../api'
// import type { UserEntity } from '../../api/models'
// import { useAuth } from '../../contexts/AuthContext'
import { Button } from '../ui/button'
import { useAuth } from '@/contexts/AuthContext';
// import { Badge } from '../ui/badge'
// import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table'
// import { toast } from 'sonner'
// // import { AdminUserUpdateForm } from '../forms/AdminUserUpdateForm'
// import { useState } from 'react'

const dashboardLinks = [
{
  label: 'User Management',
  icon: Users,
  link: '/admin/user',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_USER_ADMIN'),
},
{
  label: 'Menu Management',
  icon: Menu,
  link: '/admin/menu',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_MENU_ADMIN'),
},
{
  label: 'Finansies',
  icon: DollarSign,
  link: '/admin/finance',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_FINANCIAL_ADMIN'),
},
{
  label: 'Settings',
  icon: Settings,
  link: '/admin/settings',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_SYSTEM_ADMIN'),
},
{
  label: 'Feedback Dashboard',
  icon: MessageSquareMore,
  link: '/admin/feedback',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_FEEDBACK_ADMIN'),
},
{
  label: 'Audit Log',
  icon: NotebookText,
  link: '/admin/audit',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_AUDIT_ADMIN'),
},
{
  label: 'Orders',
  icon: ArrowUpDown,
  link: '/admin/order',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_ORDER_ADMIN'),
},
{
  label: 'Deliveries',
  icon: TruckIcon,
  link: '/admin/delivery',
  rolePredicate: (roles: String[]) => roles.includes('ROLE_DELIVERY_ADMIN'),
}
];


export function AdminDashboard() {
    const navigate = useNavigate();
    const { user, logout }= useAuth();
    const location = useLocation();

    // Default button style (orange background, white text)
    const defaultButtonClass = `
      w-full 
      justify-start
      border
      border-orange-300
      text-grey-600
      hover:text-white
      hover:bg-orange-300
      active:bg-orange-600`;
    
    // Inverted button style for active buttons (white background, orange text)
    const activeButtonClass = `
      w-full 
      justify-start
      border
      border-orange-300
      text-grey-600
      hover:text-white
      hover:bg-orange-300
      bg-orange-600`;

    const shownLinks = dashboardLinks.filter(link => link.rolePredicate(user?.roles ?? []));

    return (
        <div className="
          bg-background
          flex"
        >
            {/* Sidebar */}
            {shownLinks.length > 1 && <div className="
              w-64 
              bg-white
              shadow-lg
              h-screen
              border-r
              border-gray-200
              flex
              flex-col
              overflow-y-auto"
            >
                <div className='
                  p-4
                  border-b
                  border-gray-200'
                >
                    <h2 className='
                      text-lg
                      font-semibold
                      text-gray-900'
                    >
                      <div className='
                        flex 
                        items-center'
                      >
                        <Shield className="
                        h-4
                        w-4 
                        text-red-600 mr-3" 
                        />
                        Administration Portal
                      </div>
                    </h2>
                </div>
                <nav className='
                  flex-1
                  p-4
                  space-y-2'
                >
                    {dashboardLinks.map((link) => (
                        link.rolePredicate(user?.roles ?? []) && (
                            <Button
                                key={link.label}
                                variant="ghost"
                                className={
                                  location.pathname.startsWith(link.link) ? activeButtonClass : defaultButtonClass 
                                }
                                onClick={() => {
                                    navigate({ to: link.link });
                                }}
                            >
                                <link.icon className="h-5 w-5 mr-2" />
                                {link.label}
                            </Button>
                        )
                    ))} 
                </nav>
                <div className='
                  p-4
                  border-t
                  border-orange-300'
                >
                    <Button
                        variant="ghost"
                        className="
                          w-full 
                          justify-start
                          text-grey-600
                          hover:text-red-600
                          hover:bg-red-50"
                        onClick={ async () => {
                          await logout();
                          navigate({ to: '/login', search: { redirect: '/admin' } });
                        }}
                    >
                        <LogOut className="h-5 w-5 mr-2" />
                        Logout
                    </Button>
                </div>
            </div>}
            {/* Main Content */}
            <div className="
            flex-1 
            flex 
            flex-col 
            h-screen 
            overflow-y-auto">
                <div className="
                    flex-1
                    py-6
                    px-4"
                >
                    <Outlet />
                </div>
                {/* If the sidebar is hidden, show a logout button in the bottom left corner */}
                { shownLinks.length <= 1 && <div className='
                  fixed
                  bottom-4
                  left-4
                  bg-white
                  p-4
                  rounded
                  shadow-lg
                  border
                  border-gray-200'
                >
                    <Button
                        variant="ghost"
                        onClick={ async () => {
                          await logout();
                          navigate({ to: '/login', search: { redirect: '/admin' } });
                        }}
                    >
                        <LogOut className="h-5 w-5 mr-2" />
                        Sign Out
                    </Button>
                </div>
                }
            </div>
        </div>
    )
}
