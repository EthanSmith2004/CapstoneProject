import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Star, TrendingUp, TrendingDown, ChevronDown, ChevronUp, ChevronRight, List, ThumbsUp, ThumbsDown } from 'lucide-react';
import { formatDate } from '@/lib/utils';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import type { FeedbackPeriodStatistics, FeedbackItemStatistics } from '@/api';

interface AdminFeedbackStatsProps {
  currentStats: FeedbackPeriodStatistics;
  previousStats?: FeedbackPeriodStatistics;
  itemStats: FeedbackItemStatistics[];
  startDate: Date;
  endDate: Date;
  contrastName?: string;
}

export function AdminFeedbackStats({ 
  currentStats, 
  previousStats, 
  itemStats, 
  startDate, 
  endDate,
  contrastName 
}: AdminFeedbackStatsProps) {
  const navigate = useNavigate();
  const [showAllItems, setShowAllItems] = useState(false);

  // Helper to render star rating
  const renderStarRating = (rating: number) => {
    return (
      <div className="flex items-center gap-1">
        {[...Array(5)].map((_, index) => (
          <Star
            key={index}
            className={`h-4 w-4 ${
              index < Math.floor(rating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
            }`}
          />
        ))}
      </div>
    );
  };

  // Helper to render comparison indicator
  const ComparisonIndicator = ({ current, previous, suffix = '' }: { current: number; previous?: number; suffix?: string }) => {
    if (!previous || previous === 0) return null;
    
    const change = ((current - previous) / previous) * 100;
    const isPositive = change > 0;
    
    return (
      <div className={`flex items-center gap-1 text-sm ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
        {isPositive ? <TrendingUp className="h-4 w-4" /> : <TrendingDown className="h-4 w-4" />}
        <span>{Math.abs(change).toFixed(1)}%{suffix}</span>
      </div>
    );
  };

  // Navigate to list view with filters
  const navigateToList = (params: { startDate?: Date; endDate?: Date; minRating?: number; maxRating?: number; menuItem?: string }) => {
    const searchParams: Record<string, string | number> = {};
    if (params.startDate) searchParams.startDate = params.startDate.toISOString();
    if (params.endDate) searchParams.endDate = params.endDate.toISOString();
    if (params.minRating) searchParams.minRating = params.minRating;
    if (params.maxRating) searchParams.maxRating = params.maxRating;
    if (params.menuItem) searchParams.menuItem = params.menuItem;

    navigate({ to: '/admin/feedback/list', search: searchParams as any });
  };

  // Calculate rating distribution from item stats
  const totalRatingCounts = itemStats.reduce((acc, item) => {
    Object.entries(item.ratingDistribution || {}).forEach(([rating, count]) => {
      acc[parseInt(rating)] = (acc[parseInt(rating)] || 0) + (count as number);
    });
    return acc;
  }, {} as Record<number, number>);

  return (
    <div className="space-y-6">
      {/* Overall Rating Metrics - Top Row */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {/* Average Rating Card */}
        <Card className='bg-white'>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Average Rating
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-2">
              <div className="text-4xl font-bold">
                {currentStats.averageRating?.toFixed(1) || '0.0'}
              </div>
              <div className="flex">
                {renderStarRating(currentStats.averageRating || 0)}
              </div>
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              {formatDate(startDate.toISOString())} - {formatDate(endDate.toISOString())}
            </p>
            {previousStats && (
              <ComparisonIndicator 
                current={currentStats.averageRating || 0} 
                previous={previousStats.averageRating || 0} 
              />
            )}
          </CardContent>
        </Card>

        {/* Total Feedback Card */}
        <Card className='bg-white'>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Total Feedback
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-4xl font-bold">{currentStats.totalFeedbackCount || 0}</div>
            <p className="text-xs text-muted-foreground mt-1">
              For the selected period
            </p>
            {previousStats && (
              <ComparisonIndicator 
                current={currentStats.totalFeedbackCount || 0} 
                previous={previousStats.totalFeedbackCount || 0} 
              />
            )}
          </CardContent>
        </Card>

        {/* Satisfaction Rate Card */}
        <Card className='bg-white'>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Satisfaction Rate
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-4xl font-bold text-green-600">
              {currentStats.satisfactionRate?.toFixed(1) || '0.0'}%
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              {currentStats.positiveFeedbackCount || 0} positive out of {currentStats.totalFeedbackCount || 0}
            </p>
            {previousStats && (
              <ComparisonIndicator 
                current={currentStats.satisfactionRate || 0} 
                previous={previousStats.satisfactionRate || 0}
                suffix=" points"
              />
            )}
          </CardContent>
        </Card>

        {/* Rating Distribution Visualization */}
        <Card className='bg-white'>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Rating Distribution
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-1">
              {[5, 4, 3, 2, 1].map(rating => {
                const count = totalRatingCounts[rating] || 0;
                const percentage = currentStats.totalFeedbackCount 
                  ? (count / (currentStats.totalFeedbackCount || 1)) * 100 
                  : 0;
                
                return (
                  <div key={rating} className="flex items-center gap-2">
                    <span className="text-sm w-4">{rating}</span>
                    <Star className="h-3 w-3 fill-yellow-400 text-yellow-400" />
                    <div className="flex-1 bg-gray-200 rounded-full h-2">
                      <div 
                        className="bg-yellow-400 h-2 rounded-full transition-all" 
                        style={{ width: `${percentage}%` }}
                      />
                    </div>
                    <span className="text-xs text-muted-foreground w-8 text-right">
                      {count}
                    </span>
                  </div>
                );
              })}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Per-Item Ratings Breakdown */}
      <Card className='bg-white'>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>Rating by Item</CardTitle>
            <Button 
              variant="outline" 
              size="sm"
              onClick={() => setShowAllItems(!showAllItems)}
            >
              {showAllItems ? (
                <>
                  <ChevronUp className="mr-2 h-4 w-4" />
                  Hide
                </>
              ) : (
                <>
                  <ChevronDown className="mr-2 h-4 w-4" />
                  Show All ({itemStats.length})
                </>
              )}
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          {itemStats.length === 0 ? (
            <p className="text-muted-foreground text-center py-4">
              No item-specific feedback in this period
            </p>
          ) : (
            <div className="space-y-3">
              {(showAllItems ? itemStats : itemStats.slice(0, 5)).map(item => (
                <div key={item.menuItemName} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <div className="flex-1">
                    <div className="font-medium">{item.menuItemName}</div>
                    <div className="flex items-center gap-2 mt-1">
                      {renderStarRating(item.averageRating || 0)}
                      <span className="text-sm font-medium ml-1">
                        {item.averageRating?.toFixed(1) || '0.0'}/5
                      </span>
                      <span className="text-xs text-muted-foreground">
                        ({item.feedbackCount} reviews)
                      </span>
                    </div>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => navigateToList({ 
                      startDate, 
                      endDate, 
                      menuItem: item.menuItemName 
                    })}
                  >
                    View Feedback
                    <ChevronRight className="ml-2 h-4 w-4" />
                  </Button>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Period Comparison View */}
      {contrastName && previousStats && (
        <Card className="bg-white">
          <CardHeader>
            <CardTitle>Period Comparison</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-3 gap-4 items-center">
              <div>
                <div className="text-sm text-muted-foreground mb-2">{contrastName}</div>
                <div className="text-2xl font-bold">{previousStats.averageRating?.toFixed(1) || '0.0'}</div>
                <div className="text-sm">{previousStats.totalFeedbackCount || 0} reviews</div>
                <div className="text-sm text-muted-foreground">
                  {previousStats.satisfactionRate?.toFixed(1) || '0.0'}% satisfied
                </div>
              </div>
              <div className="flex items-center justify-center">
                <ChevronRight className="h-8 w-8 text-muted-foreground" />
              </div>
              <div>
                <div className="text-sm text-muted-foreground mb-2">Current Period</div>
                <div className="text-2xl font-bold">{currentStats.averageRating?.toFixed(1) || '0.0'}</div>
                <div className="text-sm">{currentStats.totalFeedbackCount || 0} reviews</div>
                <div className="text-sm text-muted-foreground">
                  {currentStats.satisfactionRate?.toFixed(1) || '0.0'}% satisfied
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Action Buttons */}
      <Card className='bg-gray-50'>
        <CardContent className="">
          <div className="flex flex-wrap gap-4">
            <Button 
              onClick={() => navigateToList({ startDate, endDate })}
            >
              <List className="mr-2 h-4 w-4" />
              View All Feedback for Period
            </Button>
            
            <Button 
              variant="outline"
              onClick={() => navigateToList({ startDate, endDate, minRating: 4 })}
            >
              <ThumbsUp className="mr-2 h-4 w-4" />
              View Positive Feedback
            </Button>
            
            <Button 
              variant="outline"
              onClick={() => navigateToList({ startDate, endDate, maxRating: 2 })}
            >
              <ThumbsDown className="mr-2 h-4 w-4" />
              View Negative Feedback
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
