import { useNavigate } from "@tanstack/react-router"
import { ArrowLeft, Wallet } from "lucide-react" // 👈 Add Wallet icon import

interface AppBarProps {
  title: string
  balance: number
  hideUserIcon?: boolean
  hideBalance?: boolean
}

export function AppBar({
  title,
  balance,
  hideUserIcon = false
}: AppBarProps) {
  const navigate = useNavigate()

  return (
    <div className="
      bg-orange-600 
      py-4 
      px-4 
      flex 
      items-center 
      sticky 
      top-0 
      w-full 
      z-50"
    >

      {hideUserIcon ? (
        <div className="w-8 h-8 z-10" />
      ) : (
        <ArrowLeft
          className="h-8 w-8 text-white cursor-pointer z-10"
          onClick={() => window.history.back()}
        />
      )}

     
      <h1 className="text-xl font-bold text-white absolute left-1/2 -translate-x-1/2">{title}</h1>

      
      <div
        className="flex items-center gap-2 cursor-pointer ml-auto z-10"
        onClick={() => navigate({ to: '/user/account' })}
      >
        <Wallet className="h-5 w-5 text-white" />
        <span className="text-sm text-white font-semibold">
          R{balance.toFixed(2)}
        </span>
      </div>
    </div>
  )
}
