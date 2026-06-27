import { useMemo, useState, useRef, useEffect } from "react";
import * as d3 from "d3";
import type { ColumnFiltersState } from "@tanstack/react-table";
import { formatDate } from "@/lib/utils";
import { Button } from "../ui/button";

interface TimeSeriesGraphProps {
    data: any[];
    dateKey: string;
    valueKeys: { key: string, label: string }[];
    enabledAggregations: ('hourly' | 'daily')[];
    aggregationType?: 'sum' | 'average';
    width?: number;
    height?: number;
    margin?: { top: number; right: number; bottom: number; left: number };
    chartType?: 'line' | 'histogram';
    filterState?: ColumnFiltersState;
    setColumnFilters?: (filters: ColumnFiltersState) => void;
}

interface AggregatedDataPoint {
    date: Date;
    [key: string]: any;
}

export function TimeSeriesGraph({
    data,
    dateKey,
    valueKeys,
    enabledAggregations,
    aggregationType = 'sum',
    width: propWidth,
    height: propHeight,
    margin = { top: 20, right: 50, bottom: 80, left: 50 },
    chartType = 'line',
    filterState,
    setColumnFilters
}: TimeSeriesGraphProps) {
    const svgRef = useRef<SVGSVGElement>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const [selectedAggregation, setSelectedAggregation] = useState<'hourly' | 'daily'>(enabledAggregations[0]);
    const [selectedValueKey, setSelectedValueKey] = useState<string>(valueKeys[0]?.key || '');
    const [isDragging, setIsDragging] = useState(false);
    const [dragStart, setDragStart] = useState<{ x: number, date: Date } | null>(null);
    const [dragEnd, setDragEnd] = useState<{ x: number, date: Date } | null>(null);
    const [dimensions, setDimensions] = useState({ width: propWidth || 800, height: propHeight || 400 });

    // Use actual dimensions or fallback to props/defaults
    const width = dimensions.width;
    const height = dimensions.height;

    // Responsive resize observer
    useEffect(() => {
        if (!containerRef.current) return;

        const resizeObserver = new ResizeObserver((entries) => {
            for (const entry of entries) {
                const { width: containerWidth } = entry.contentRect;
                // Use container width, but respect minimum size and prop overrides
                const newWidth = propWidth || Math.max(300, containerWidth);
                const newHeight = propHeight || 400;
                
                setDimensions({ width: newWidth, height: newHeight });
            }
        });

        resizeObserver.observe(containerRef.current);

        return () => {
            resizeObserver.disconnect();
        };
    }, [propWidth, propHeight]);

    // Helper function to update date filter
    const updateDateFilter = (startDate: Date, endDate: Date) => {
        if (!setColumnFilters) return;

        // Find existing date filter for this column
        const currentFilters = filterState || [];
        const newFilters = currentFilters.filter(filter => filter.id !== dateKey);
        
        // Add new date range filter
        newFilters.push({
            id: dateKey,
            value: [startDate.toISOString(), endDate.toISOString()]
        });

        setColumnFilters(newFilters);
    };

    const aggregatedData = useMemo(() => {
        if (!data?.length) return [];

        // Group data by time period
        const grouped = new Map<string, any>();

        data.forEach(item => {
            const date = new Date(item[dateKey]);
            let timeKey: string;

            if (selectedAggregation === 'hourly') {
                timeKey = d3.timeFormat('%Y-%m-%d %H:00')(date);
            } else {
                timeKey = d3.timeFormat('%Y-%m-%d')(date);
            }

            if (!grouped.has(timeKey)) {
                const aggregatedPoint: AggregatedDataPoint = {
                    date: d3.timeParse(selectedAggregation === 'hourly' ? '%Y-%m-%d %H:%M' : '%Y-%m-%d')(timeKey)!
                };
                
                valueKeys.forEach(({ key }) => {
                    aggregatedPoint[key] = 0;
                    aggregatedPoint[`${key}_count`] = 0;
                });
                
                grouped.set(timeKey, aggregatedPoint);
            }

            const point = grouped.get(timeKey)!;
            valueKeys.forEach(({ key }) => {
                if (typeof item[key] === 'number') {
                    point[key] += item[key];
                    point[`${key}_count`] += 1;
                }
            });
        });

        // Convert to array and apply aggregation type
        return Array.from(grouped.values())
            .map(point => {
                const finalPoint = { ...point };
                valueKeys.forEach(({ key }) => {
                    if (point[`${key}_count`] > 0) {
                        if (aggregationType === 'average') {
                            finalPoint[key] = point[key] / point[`${key}_count`];
                        } else {
                            // For sum, keep the accumulated value
                            finalPoint[key] = point[key];
                        }
                    }
                });
                return finalPoint;
            })
            .sort((a, b) => a.date.getTime() - b.date.getTime());
    }, [data, dateKey, valueKeys, selectedAggregation, aggregationType]);

    useEffect(() => {
        if (!svgRef.current || !aggregatedData.length) return;

        const svg = d3.select(svgRef.current);
        svg.selectAll("*").remove();

        const innerWidth = width - margin.left - margin.right;
        const innerHeight = height - margin.top - margin.bottom;

        const g = svg.append("g")
            .attr("transform", `translate(${margin.left},${margin.top})`);

        // Scales - use different scales for different chart types
        let xScale: d3.ScaleTime<number, number> | d3.ScaleBand<string>;

        if (chartType === 'histogram') {
            // For histogram, use band scale for even spacing
            xScale = d3.scaleBand()
                .domain(aggregatedData.map((_, i) => i.toString()))
                .range([0, innerWidth])
                .padding(0.1);
        } else {
            // For line chart, use time scale
            xScale = d3.scaleTime()
                .domain(d3.extent(aggregatedData, d => d.date) as [Date, Date])
                .range([0, innerWidth]);
        }

        // Get values for the selected key only
        const selectedValues = aggregatedData.map(d => d[selectedValueKey]).filter(v => typeof v === 'number');
        
        const yScale = d3.scaleLinear()
            .domain(d3.extent(selectedValues) as [number, number])
            .nice()
            .range([innerHeight, 0]);

        // Color for the selected series
        const color = '#3b82f6'; // Blue color

        // Add axes with smart tick spacing
        let xAxis: d3.Axis<any>;
        let xGrid: d3.Axis<any>;

        if (chartType === 'histogram') {
            const bandScale = xScale as d3.ScaleBand<string>;
            const numDataPoints = aggregatedData.length;
            
            // Calculate optimal tick interval based on available space
            // Aim for ~50-80 pixels per tick to avoid overlap
            const tickWidth = 50;
            const maxTicks = Math.floor(innerWidth / tickWidth);
            const tickInterval = Math.max(1, Math.ceil(numDataPoints / maxTicks));
            
            // For histogram, show date labels with dynamic interval
            xAxis = d3.axisBottom(bandScale)
                .tickValues(
                    aggregatedData
                        .map((_, i) => i)
                        .filter(i => i % tickInterval === 0)
                        .map(i => i.toString())
                )
                .tickFormat((d) => {
                    const date = aggregatedData[parseInt(d)].date;
                    return d3.timeFormat(selectedAggregation === 'hourly' ? '%m/%d %H:%M' : '%m/%d')(date);
                });
            
            xGrid = d3.axisBottom(bandScale)
                .tickValues(
                    aggregatedData
                        .map((_, i) => i)
                        .filter(i => i % tickInterval === 0)
                        .map(i => i.toString())
                )
                .tickSize(-innerHeight)
                .tickFormat(() => "");
        } else {
            // For line chart, use time formatting with adaptive ticks
            const timeScale = xScale as d3.ScaleTime<number, number>;
            const tickWidth = 60;
            const maxTicks = Math.floor(innerWidth / tickWidth);
            
            xAxis = d3.axisBottom(timeScale)
                .ticks(maxTicks)
                .tickFormat((d) => d3.timeFormat(selectedAggregation === 'hourly' ? '%H:%M' : '%m/%d')(d as Date));
            
            xGrid = d3.axisBottom(timeScale)
                .ticks(maxTicks)
                .tickSize(-innerHeight)
                .tickFormat(() => "");
        }

        g.append("g")
            .attr("transform", `translate(0,${innerHeight})`)
            .call(xAxis)
            .selectAll("text")
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".15em")
            .attr("transform", "rotate(-45)");

        g.append("g")
            .call(d3.axisLeft(yScale));

        // Add alternating background bands for time grouping
        if (chartType === 'histogram') {
            const bandScale = xScale as d3.ScaleBand<string>;
            const bandwidth = bandScale.bandwidth();
            
            // Group by 12-hour periods for hourly, or by week for daily
            let currentGroup = 0;
            let lastGroupValue = -1;
            
            aggregatedData.forEach((d, i) => {
                let groupValue: number;
                if (selectedAggregation === 'hourly') {
                    // Group by 12-hour periods (0-11, 12-23)
                    groupValue = Math.floor(d.date.getHours() / 12) + d.date.getDate() * 2 + d.date.getMonth() * 60;
                } else {
                    // Group by week
                    const weekNum = d3.timeWeek.count(d3.timeYear(d.date), d.date);
                    groupValue = weekNum + d.date.getFullYear() * 52;
                }
                
                if (groupValue !== lastGroupValue) {
                    currentGroup++;
                    lastGroupValue = groupValue;
                }
                
                const x = bandScale(i.toString()) || 0;
                const isEvenGroup = currentGroup % 2 === 0;
                
                g.append("rect")
                    .attr("class", "time-group-bg")
                    .attr("x", x)
                    .attr("y", 0)
                    .attr("width", bandwidth)
                    .attr("height", innerHeight)
                    .attr("fill", isEvenGroup ? "rgba(59, 130, 246, 0.05)" : "rgba(59, 130, 246, 0.02)")
                    .attr("pointer-events", "none")
                    .lower(); // Move to back
            });
        }

        // Add grid lines
        g.append("g")
            .attr("class", "grid")
            .attr("transform", `translate(0,${innerHeight})`)
            .call(xGrid)
            .style("stroke-dasharray", "3,3")
            .style("opacity", 0.3);

        const yGrid = d3.axisLeft(yScale)
            .tickSize(-innerWidth)
            .tickFormat(() => "");

        g.append("g")
            .attr("class", "grid")
            .call(yGrid)
            .style("stroke-dasharray", "3,3")
            .style("opacity", 0.3);

        // Helper function to determine time group for alternating colors
        const getBarOpacity = (_date: Date, index: number): number => {
            // Count group changes up to this point
            let groupCount = 0;
            
            for (let i = 0; i <= index; i++) {
                const d = aggregatedData[i].date;
                let currentGroupVal: number;
                
                if (selectedAggregation === 'hourly') {
                    // Group by 12-hour periods (0-11, 12-23)
                    currentGroupVal = Math.floor(d.getHours() / 12) + d.getDate() * 2 + d.getMonth() * 60;
                } else {
                    // Group by week
                    const weekNum = d3.timeWeek.count(d3.timeYear(d), d);
                    currentGroupVal = weekNum + d.getFullYear() * 52;
                }
                
                if (i > 0) {
                    const prevD = aggregatedData[i - 1].date;
                    let prevGroupVal: number;
                    
                    if (selectedAggregation === 'hourly') {
                        prevGroupVal = Math.floor(prevD.getHours() / 12) + prevD.getDate() * 2 + prevD.getMonth() * 60;
                    } else {
                        const weekNum = d3.timeWeek.count(d3.timeYear(prevD), prevD);
                        prevGroupVal = weekNum + prevD.getFullYear() * 52;
                    }
                    
                    if (currentGroupVal !== prevGroupVal) {
                        groupCount++;
                    }
                }
            }
            
            return groupCount % 2 === 0 ? 0.85 : 0.65;
        };

        // Render chart based on type
        if (chartType === 'line') {
            // Line chart - use time scale
            const timeScale = xScale as d3.ScaleTime<number, number>;
            
            const lineGenerator = d3.line<AggregatedDataPoint>()
                .x(d => timeScale(d.date))
                .y(d => yScale(d[selectedValueKey] || 0))
                .curve(d3.curveMonotoneX);

            g.append("path")
                .datum(aggregatedData)
                .attr("fill", "none")
                .attr("stroke", color)
                .attr("stroke-width", 2)
                .attr("d", lineGenerator);

            // Add dots with alternating opacity
            g.selectAll(".dot")
                .data(aggregatedData)
                .enter().append("circle")
                .attr("class", "dot")
                .attr("cx", d => timeScale(d.date))
                .attr("cy", d => yScale(d[selectedValueKey] || 0))
                .attr("r", 3)
                .attr("fill", color)
                .attr("opacity", (d, i) => getBarOpacity(d.date, i));
        } else {
            // Histogram/Bar chart - use band scale with alternating saturation
            const bandScale = xScale as d3.ScaleBand<string>;

            g.selectAll(".bar")
                .data(aggregatedData)
                .enter().append("rect")
                .attr("class", "bar")
                .attr("x", (_, i) => bandScale(i.toString()) || 0)
                .attr("y", d => yScale(d[selectedValueKey] || 0))
                .attr("width", bandScale.bandwidth())
                .attr("height", d => innerHeight - yScale(d[selectedValueKey] || 0))
                .attr("fill", color)
                .attr("opacity", (d, i) => getBarOpacity(d.date, i));
        }

        // Add tooltip
        d3.select("body").selectAll(".tooltip-timeseries")
            .data([0])
            .enter()
            .append("div")
            .attr("class", "tooltip-timeseries")
            .style("position", "absolute")
            .style("visibility", "hidden")
            .style("background", "rgba(0, 0, 0, 0.8)")
            .style("color", "white")
            .style("padding", "8px")
            .style("border-radius", "4px")
            .style("font-size", "12px")
            .style("pointer-events", "none")
            .style("z-index", "1000");

        // Helper functions for mouse interaction
        const getDateFromMouseX = (mouseX: number): Date | null => {
            if (chartType === 'line') {
                const timeScale = xScale as d3.ScaleTime<number, number>;
                return timeScale.invert(mouseX);
            } else {
                // For histogram, find the closest bar
                const bandScale = xScale as d3.ScaleBand<string>;
                const bandwidth = bandScale.bandwidth();
                let closestIndex = -1;
                let minDistance = Infinity;

                aggregatedData.forEach((_, i) => {
                    const barX = bandScale(i.toString()) || 0;
                    const barCenter = barX + bandwidth / 2;
                    const distance = Math.abs(mouseX - barCenter);
                    if (distance < minDistance && distance < bandwidth / 2) {
                        minDistance = distance;
                        closestIndex = i;
                    }
                });

                return closestIndex >= 0 ? aggregatedData[closestIndex].date : null;
            }
        };

        const getDataPointFromMouseX = (mouseX: number): AggregatedDataPoint | null => {
            if (chartType === 'line') {
                const timeScale = xScale as d3.ScaleTime<number, number>;
                const x0 = timeScale.invert(mouseX);
                const bisectDate = d3.bisector((d: AggregatedDataPoint) => d.date).left;
                const i = bisectDate(aggregatedData, x0, 1);
                const d0 = aggregatedData[i - 1];
                const d1 = aggregatedData[i];
                return d1 && (x0.getTime() - d0?.date.getTime() > d1.date.getTime() - x0.getTime()) ? d1 : d0;
            } else {
                const bandScale = xScale as d3.ScaleBand<string>;
                const bandwidth = bandScale.bandwidth();
                let closestIndex = -1;
                let minDistance = Infinity;

                aggregatedData.forEach((_, i) => {
                    const barX = bandScale(i.toString()) || 0;
                    const barCenter = barX + bandwidth / 2;
                    const distance = Math.abs(mouseX - barCenter);
                    if (distance < minDistance && distance < bandwidth / 2) {
                        minDistance = distance;
                        closestIndex = i;
                    }
                });

                return closestIndex >= 0 ? aggregatedData[closestIndex] : null;
            }
        };

        // Add drag selection overlay
        const dragSelection = g.append("rect")
            .attr("class", "drag-selection")
            .attr("fill", "rgba(0, 123, 255, 0.2)")
            .attr("stroke", "#007bff")
            .attr("stroke-width", 1)
            .attr("stroke-dasharray", "3,3")
            .style("display", "none");

        // Add invisible overlay for mouse events
        const overlay = g.append("rect")
            .attr("width", innerWidth)
            .attr("height", innerHeight)
            .attr("fill", "none")
            .attr("pointer-events", "all")
            .style("cursor", "crosshair");

        // Mouse event handlers
        overlay
            .on("mousedown", function(event) {
                if (!setColumnFilters) return;
                
                const [mouseX] = d3.pointer(event);
                const date = getDateFromMouseX(mouseX);
                
                if (date) {
                    setIsDragging(true);
                    setDragStart({ x: mouseX, date });
                    setDragEnd(null);
                    
                    dragSelection
                        .style("display", null)
                        .attr("x", mouseX)
                        .attr("y", 0)
                        .attr("width", 0)
                        .attr("height", innerHeight);
                }
                
                event.preventDefault();
            })
            .on("mousemove", function(event) {
                const [mouseX] = d3.pointer(event);
                
                if (isDragging && dragStart) {
                    // Update drag selection rectangle
                    const startX = Math.min(dragStart.x, mouseX);
                    const endX = Math.max(dragStart.x, mouseX);
                    
                    dragSelection
                        .attr("x", startX)
                        .attr("width", endX - startX);
                        
                    const endDate = getDateFromMouseX(mouseX);
                    if (endDate) {
                        setDragEnd({ x: mouseX, date: endDate });
                    }
                } else {
                    // Show tooltip when not dragging
                    const d = getDataPointFromMouseX(mouseX);
                    
                    if (d) {
                        const selectedLabel = valueKeys.find(v => v.key === selectedValueKey)?.label || selectedValueKey;
                        const aggregationLabel = aggregationType === 'sum' ? 'Sum' : 'Gemiddelde';
                        const tooltipContent = [
                            `Date: ${d3.timeFormat(selectedAggregation === 'hourly' ? '%Y-%m-%d %H:%M' : '%Y-%m-%d')(d.date)}`,
                            `${selectedLabel} (${aggregationLabel}): ${d[selectedValueKey]?.toFixed(2) || 0}`
                        ].join('<br>');

                        d3.select(".tooltip-timeseries")
                            .style("visibility", "visible")
                            .html(tooltipContent)
                            .style("left", (event.pageX + 10) + "px")
                            .style("top", (event.pageY - 10) + "px");
                    } else {
                        d3.select(".tooltip-timeseries").style("visibility", "hidden");
                    }
                }
            })
            .on("mouseup", function() {
                if (isDragging && dragStart && dragEnd) {
                    // Apply date range filter
                    const startTime = dragStart.date.getTime();
                    const endTime = dragEnd.date.getTime();
                    
                    // Only apply filter if there's a meaningful range (more than 1 minute difference)
                    if (Math.abs(endTime - startTime) > 60000) {
                        const startDate = new Date(Math.min(startTime, endTime));
                        const endDate = new Date(Math.max(startTime, endTime));
                        updateDateFilter(startDate, endDate);
                    }
                }
                
                // Reset drag state
                setIsDragging(false);
                setDragStart(null);
                setDragEnd(null);
                dragSelection.style("display", "none");
            })
            .on("click", function(event) {
                if (!setColumnFilters || isDragging) return;
                
                const [mouseX] = d3.pointer(event);
                const clickedPoint = getDataPointFromMouseX(mouseX);
                
                if (clickedPoint && chartType === 'histogram') {
                    // For histogram bars, select just that time period
                    let startDate: Date, endDate: Date;
                    
                    if (selectedAggregation === 'hourly') {
                        startDate = new Date(clickedPoint.date);
                        endDate = new Date(clickedPoint.date.getTime() + 60 * 60 * 1000); // Add 1 hour
                    } else {
                        startDate = new Date(clickedPoint.date);
                        endDate = new Date(clickedPoint.date.getTime() + 24 * 60 * 60 * 1000); // Add 1 day
                    }
                    
                    updateDateFilter(startDate, endDate);
                }
            })
            .on("mouseout", function() {
                if (!isDragging) {
                    d3.select(".tooltip-timeseries").style("visibility", "hidden");
                }
            });

    }, [aggregatedData, width, height, margin, selectedValueKey, chartType, aggregationType, isDragging, dragStart, dragEnd, setColumnFilters, filterState, updateDateFilter]);

    return (
        <div ref={containerRef} className="w-full space-y-4">
            <div className="flex flex-wrap gap-4 items-center">
                {/* Value Key Selector */}
                <div className="flex items-center gap-2">
                    <label htmlFor="value-select" className="text-sm font-medium text-gray-700">
                        Data Series:
                    </label>
                    <select
                        id="value-select"
                        value={selectedValueKey}
                        onChange={(e) => setSelectedValueKey(e.target.value)}
                        className="px-3 py-1 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        {valueKeys.map(({ key, label }) => (
                            <option key={key} value={key}>
                                {label}
                            </option>
                        ))}
                    </select>
                </div>
                {/* Time Aggregation Toggle */}
                {enabledAggregations.length > 1 && (
                    <div className="flex gap-2">
                        {enabledAggregations.map(agg => (
                            <Button
                                key={agg}
                                onClick={() => setSelectedAggregation(agg)}
                                variant={agg == selectedAggregation ? 'default' : 'outline'}
                            >
                                {agg === 'hourly' ? 'Hourly' : 'Daily'}
                            </Button>
                        ))}
                    </div>
                )}
                
                {/* Filter Controls */}
                {setColumnFilters && (() => {
                    const currentDateFilter = filterState?.find(filter => filter.id === dateKey);
                    const hasDateFilter = currentDateFilter && Array.isArray(currentDateFilter.value) && 
                                         currentDateFilter.value.some(v => v !== undefined);
                    
                    if (hasDateFilter) {
                        const [startDate, endDate] = currentDateFilter.value as [string | undefined, string | undefined];
                        return (
                            <div className="flex items-center gap-2 text-sm">
                                <span className="text-gray-600">Filter:</span>
                                <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded">
                                    {startDate && endDate ? `${formatDate(startDate)} to ${formatDate(endDate)}` : 
                                     startDate ? `From ${formatDate(startDate)}` : 
                                     endDate ? `To ${formatDate(endDate)}` : ''}
                                </span>
                                <Button
                                    onClick={() => {
                                        const newFilters = (filterState || []).filter(filter => filter.id !== dateKey);
                                        setColumnFilters(newFilters);
                                    }}
                                    variant='destructive'
                                >
                                    Remove Filter
                                </Button>
                            </div>
                        );
                    }
                    return null;
                })()}
            </div>
            
            {/* Instructions */}
            {setColumnFilters && (
                <div className="text-xs text-gray-500 space-y-1">
                    <strong>Drag</strong> to select a date range
                </div>
            )}
            
            <div className="w-full overflow-x-auto">
                <svg
                    ref={svgRef}
                    width={width}
                    height={height}
                    className="border border-gray-200 rounded"
                />
            </div>
        </div>
    );
}
