/*
 * MACDChart.java
 * Android-Charts
 *
 * Created by limc on 2014.
 *
 * Copyright 2011 limc.cn All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.limc.androidcharts.view;

import cn.limc.androidcharts.entity.IMeasurable;
import cn.limc.androidcharts.entity.MACDEntity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

/**
 * <p>
 * en
 * </p>
 * <p>
 * jp
 * </p>
 * <p>
 * cn
 * </p>
 * 
 * @author limc
 * @version v1.0 2014/03/17 17:19:45
 * 
 */
public class MACDChart extends SlipStickChart {

	public static final int MACD_DISPLAY_TYPE_STICK = 1 << 0;
	public static final int MACD_DISPLAY_TYPE_LINE = 1 << 1;
	public static final int MACD_DISPLAY_TYPE_LINE_STICK = 1 << 2;

	public static final int DEFAULT_POSITIVE_STICK_COLOR = Color.RED;
	public static final int DEFAULT_NEGATIVE_STICK_COLOR = Color.BLUE;
	public static final int DEFAULT_MACD_LINE_COLOR = Color.RED;
	public static final int DEFAULT_DIFF_LINE_COLOR = Color.WHITE;
	public static final int DEFAULT_DEA_LINE_COLOR = Color.YELLOW;
	public static final int DEFAULT_MACD_DISPLAY_TYPE = MACD_DISPLAY_TYPE_LINE_STICK;

	private int positiveStickColor = DEFAULT_POSITIVE_STICK_COLOR;
	private int negativeStickColor = DEFAULT_NEGATIVE_STICK_COLOR;
	private int macdLineColor = DEFAULT_MACD_LINE_COLOR;
	private int diffLineColor = DEFAULT_DIFF_LINE_COLOR;
	private int deaLineColor = DEFAULT_DEA_LINE_COLOR;
	private int macdDisplayType = DEFAULT_MACD_DISPLAY_TYPE;

	/**
	 * <p>
	 * Constructor of MACDChart
	 * </p>
	 * <p>
	 * MACDChart类对象的构造函数
	 * </p>
	 * <p>
	 * MACDChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 */
	public MACDChart(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p>
	 * Constructor of MACDChart
	 * </p>
	 * <p>
	 * MACDChart类对象的构造函数
	 * </p>
	 * <p>
	 * MACDChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MACDChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p>
	 * Constructor of MACDChart
	 * </p>
	 * <p>
	 * MACDChart类对象的构造函数
	 * </p>
	 * <p>
	 * MACDChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 */
	public MACDChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void calcValueRange() {
		if (this.stickData == null) {
			return;
		}
		if (this.stickData.size() <= 0) {
			return;
		}
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;

		IMeasurable first = this.stickData.get(this.displayFrom);
		maxValue = Math.max(first.getHigh(), maxValue);
		minValue = Math.min(first.getLow(), minValue);
		// 判断显示为方柱或显示为线条
		for (int i = this.displayFrom; i < this.displayFrom
				+ this.displayNumber; i++) {
			IMeasurable macd = this.stickData.get(i);
			maxValue = Math.max(macd.getHigh(), maxValue);
			minValue = Math.min(macd.getLow(), minValue);
		}
		this.maxValue = maxValue;
		this.minValue = minValue;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 在K线图上增加均线
		this.drawLinesData(canvas);
	}

	@Override
	protected void drawSticks(Canvas canvas) {

		if (stickData == null) {
			return;
		}
		if (stickData.size() <= 0) {
			return;
		}

		Paint mPaintStick = new Paint();
		mPaintStick.setAntiAlias(true);

		if (this.axisYPosition == AXIS_Y_POSITION_LEFT) {
			// 蜡烛棒宽度
			float stickWidth = ((super.getWidth() - this.axisMarginLeft - 2 * this.axisMarginRight) / this.displayNumber) - 1;

			// 蜡烛棒起始绘制位置
			float stickX = this.axisMarginLeft + this.axisMarginRight + 1;

			PointF prePoint = new PointF(0, 0);
			// 判断显示为方柱或显示为线条
			for (int i = this.displayFrom; i < this.displayFrom
					+ this.displayNumber; i++) {
				MACDEntity stick = (MACDEntity) this.stickData.get(i);

				float highY = (float) ((1 - (stick.getMacd() - this.minValue)
						/ (this.maxValue - this.minValue))
						* (super.getHeight() - this.axisMarginBottom) - super.axisMarginTop);
				float lowY = (float) ((1 - (0 - this.minValue)
						/ (this.maxValue - this.minValue))
						* (super.getHeight() - this.axisMarginBottom) - this.axisMarginTop);

				if (stick.getMacd() == 0) {
					// 没有值的情况下不绘制
					continue;
				}

				// 柱状线颜色设定
				if (stick.getMacd() > 0) {
					mPaintStick.setColor(this.positiveStickColor);
				} else {
					mPaintStick.setColor(this.negativeStickColor);
				}

				if (this.macdDisplayType == MACD_DISPLAY_TYPE_STICK) {
					// 绘制数据，根据宽度判断绘制直线或方柱
					if (stickWidth >= 2) {
						canvas.drawRect(stickX, highY, stickWidth,
								lowY - highY, mPaintStick);
					} else {
						canvas.drawLine(stickX, highY, stickX, lowY,
								mPaintStick);
					}
				} else if (this.macdDisplayType == MACD_DISPLAY_TYPE_LINE) {
					mPaintStick.setColor(this.macdLineColor);
					// 绘制线条
					if (i == this.displayFrom) {
						prePoint = new PointF(stickX + stickWidth / 2, highY);
					} else {
						canvas.drawLine(prePoint.x, prePoint.y, stickX
								+ stickWidth / 2, highY, mPaintStick);
						prePoint = new PointF(stickX + stickWidth / 2, highY);
					}
				} else if (this.macdDisplayType == MACD_DISPLAY_TYPE_LINE_STICK) {
					canvas.drawLine(stickX + stickWidth / 2, highY, stickX
							+ stickWidth / 2, lowY, mPaintStick);
				}

				// X位移
				stickX = stickX + 1 + stickWidth;
			}
		} else {
			// float stickWidth = ((rect.size.width - 2 * this.axisMarginLeft -
			// this.axisMarginRight) / this.displayNumber) - 1;
			// // 蜡烛棒起始绘制位置
			// float stickX = rect.size.width - this.axisMarginRight - 1 -
			// stickWidth;
			// //判断显示为方柱或显示为线条
			// for (NSUInteger i = 0; i < this.displayNumber; i++) {
			// NSUInteger index = this.displayFrom + this.displayNumber - 1 - i;
			// CCSMACDData *stick = [this.stickData objectAtIndex:index];
			//
			// float highY = ((1 - (stick.macd - this.minValue) / (this.maxValue
			// - this.minValue)) * (rect.size.height - this.axisMarginBottom) -
			// super.axisMarginTop);
			// float lowY = ((1 - (0 - this.minValue) / (this.maxValue -
			// this.minValue)) * (rect.size.height - this.axisMarginBottom) -
			// this.axisMarginTop);
			//
			// if (stick.macd == 0) {
			// //没有值的情况下不绘制
			// } else {
			//
			// //柱状线颜色设定
			// if (stick.macd > 0) {
			// CGContextSetStrokeColorWithColor(context,
			// this.positiveStickColor.CGColor);
			// CGContextSetFillColorWithColor(context,
			// this.positiveStickColor.CGColor);
			// } else {
			// CGContextSetStrokeColorWithColor(context,
			// this.negativeStickColor.CGColor);
			// CGContextSetFillColorWithColor(context,
			// this.negativeStickColor.CGColor);
			// }
			//
			// if (this.macdDisplayType == CCSMACDChartDisplayTypeStick) {
			// //绘制数据，根据宽度判断绘制直线或方柱
			// if (stickWidth >= 2) {
			// CGContextAddRect(context, CGRectMake(stickX, highY, stickWidth,
			// lowY - highY));
			// //填充路径
			// CGContextFillPath(context);
			// } else {
			// CGContextMoveToPoint(context, stickX, highY);
			// CGContextAddLineToPoint(context, stickX, lowY);
			// //绘制线条
			// CGContextStrokePath(context);
			// }
			// } else if (this.macdDisplayType ==
			// CCSMACDChartDisplayTypeLineStick) {
			// CGContextMoveToPoint(context, stickX - stickWidth / 2, highY);
			// CGContextAddLineToPoint(context, stickX - stickWidth / 2, lowY);
			// //绘制线条
			// CGContextStrokePath(context);
			// } else {
			// //绘制线条
			// if (index == this.displayFrom + this.displayNumber - 1) {
			// CGContextMoveToPoint(context, stickX - stickWidth / 2, highY);
			// } else if (index == 0) {
			// CGContextAddLineToPoint(context, stickX - stickWidth / 2, highY);
			// CGContextSetStrokeColorWithColor(context,
			// this.macdLineColor.CGColor);
			// CGContextStrokePath(context);
			// } else {
			// CGContextAddLineToPoint(context, stickX - stickWidth / 2, highY);
			// }
			// }
			//
			// }
			// //X位移
			// stickX = stickX - 1 - stickWidth;
			// }
		}
	}

	protected void drawDiffLine(Canvas canvas) {
		Paint mPaintStick = new Paint();
		mPaintStick.setAntiAlias(true);
		mPaintStick.setColor(this.diffLineColor);

		// 起始位置
		float startX;
		float lastY = 0;

		// 点线距离
		float lineLength;

		if (this.axisYPosition == AXIS_Y_POSITION_LEFT) {
			lineLength = ((super.getWidth() - this.axisMarginLeft - 2 * this.axisMarginRight) / this.displayNumber);
		} else {
			lineLength = ((super.getWidth() - 2 * this.axisMarginLeft - this.axisMarginRight) / this.displayNumber);
		}

		// 起始点
		startX = super.axisMarginLeft + lineLength / 2;

		// 判断点的多少
		if (stickData.size() == 0) {
			// 0根则返回
			return;
		} else if (stickData.size() == 1) {
			// 1根则绘制一条直线
			MACDEntity lineData = (MACDEntity) stickData.get(0);
			// 获取终点Y坐标
			float valueY = (float) ((1 - (lineData.getDiff() - this.minValue)
					/ (this.maxValue - this.minValue))
					* (super.getHeight() - this.axisMarginBottom) - this.axisMarginTop);

			canvas.drawLine(startX, valueY, this.axisMarginLeft, valueY,
					mPaintStick);

		} else {
			// 遍历并绘制线条
			for (int j = this.displayFrom; j < this.displayFrom
					+ this.displayNumber; j++) {
				MACDEntity lineData = (MACDEntity) stickData.get(j);

				// 获取终点Y坐标
				float valueY = (float) ((1 - (lineData.getDiff() - this.minValue)
						/ (this.maxValue - this.minValue))
						* (super.getHeight() - this.axisMarginBottom) - this.axisMarginTop);
				// 绘制线条路径
				if (j == this.displayFrom || j == 0) {
					if (lineData.getDiff() == 0) {
					} else {
						lastY = valueY;
					}
				} else {
					MACDEntity preLineData = (MACDEntity) stickData.get(j - 1);
					if (lineData.getDiff() == 0) {
					} else {
						if (preLineData.getDiff() == 0) {
						} else {
							canvas.drawLine(startX - lineLength, lastY, startX,
									valueY, mPaintStick);
							lastY = valueY;
						}
					}
				}
				// X位移
				startX = startX + lineLength;
			}
		}
	}

	protected void drawDeaLine(Canvas canvas) {

		Paint mPaintStick = new Paint();
		mPaintStick.setAntiAlias(true);
		mPaintStick.setColor(this.deaLineColor);

		// 起始位置
		float startX;
		float lastY = 0;

		// 点线距离
		float lineLength;

		if (this.axisYPosition == AXIS_Y_POSITION_LEFT) {
			lineLength = ((super.getWidth() - this.axisMarginLeft - 2 * this.axisMarginRight) / this.displayNumber);
		} else {
			lineLength = ((super.getWidth() - 2 * this.axisMarginLeft - this.axisMarginRight) / this.displayNumber);
		}

		// 起始点
		startX = super.axisMarginLeft + lineLength / 2;

		// 判断点的多少
		if (stickData.size() == 0) {
			// 0根则返回
			return;
		} else if (stickData.size() == 1) {
			// 1根则绘制一条直线
			MACDEntity lineData = (MACDEntity) stickData.get(0);
			// 获取终点Y坐标
			float valueY = (float) ((1 - (lineData.getDea() - this.minValue)
					/ (this.maxValue - this.minValue))
					* (super.getHeight() - this.axisMarginBottom) - this.axisMarginTop);

			canvas.drawLine(startX, valueY, this.axisMarginLeft, valueY,
					mPaintStick);

		} else {
			// 遍历并绘制线条
			for (int j = this.displayFrom; j < this.displayFrom
					+ this.displayNumber; j++) {
				MACDEntity lineData = (MACDEntity) stickData.get(j);

				// 获取终点Y坐标
				float valueY = (float) ((1 - (lineData.getDea() - this.minValue)
						/ (this.maxValue - this.minValue))
						* (super.getHeight() - this.axisMarginBottom) - this.axisMarginTop);
				// 绘制线条路径
				if (j == this.displayFrom || j == 0) {
					if (lineData.getDea() == 0) {
					} else {
						lastY = valueY;
					}
				} else {
					MACDEntity preLineData = (MACDEntity) stickData.get(j - 1);
					if (lineData.getDea() == 0) {
					} else {
						if (preLineData.getDea() == 0) {
						} else {
							canvas.drawLine(startX - lineLength, lastY, startX,
									valueY, mPaintStick);
							lastY = valueY;
						}
					}
				}
				// X位移
				startX = startX + lineLength;
			}
		}
	}

	protected void drawLinesData(Canvas canvas) {

		if (stickData == null) {
			return;
		}
		if (stickData.size() <= 0) {
			return;
		}

		drawDeaLine(canvas);
		drawDiffLine(canvas);
	}

	/**
	 * @return the positiveStickColor
	 */
	public int getPositiveStickColor() {
		return positiveStickColor;
	}

	/**
	 * @param positiveStickColor
	 *            the positiveStickColor to set
	 */
	public void setPositiveStickColor(int positiveStickColor) {
		this.positiveStickColor = positiveStickColor;
	}

	/**
	 * @return the negativeStickColor
	 */
	public int getNegativeStickColor() {
		return negativeStickColor;
	}

	/**
	 * @param negativeStickColor
	 *            the negativeStickColor to set
	 */
	public void setNegativeStickColor(int negativeStickColor) {
		this.negativeStickColor = negativeStickColor;
	}

	/**
	 * @return the macdLineColor
	 */
	public int getMacdLineColor() {
		return macdLineColor;
	}

	/**
	 * @param macdLineColor
	 *            the macdLineColor to set
	 */
	public void setMacdLineColor(int macdLineColor) {
		this.macdLineColor = macdLineColor;
	}

	/**
	 * @return the diffLineColor
	 */
	public int getDiffLineColor() {
		return diffLineColor;
	}

	/**
	 * @param diffLineColor
	 *            the diffLineColor to set
	 */
	public void setDiffLineColor(int diffLineColor) {
		this.diffLineColor = diffLineColor;
	}

	/**
	 * @return the deaLineColor
	 */
	public int getDeaLineColor() {
		return deaLineColor;
	}

	/**
	 * @param deaLineColor
	 *            the deaLineColor to set
	 */
	public void setDeaLineColor(int deaLineColor) {
		this.deaLineColor = deaLineColor;
	}

	/**
	 * @return the macdDisplayType
	 */
	public int getMacdDisplayType() {
		return macdDisplayType;
	}

	/**
	 * @param macdDisplayType
	 *            the macdDisplayType to set
	 */
	public void setMacdDisplayType(int macdDisplayType) {
		this.macdDisplayType = macdDisplayType;
	}

}