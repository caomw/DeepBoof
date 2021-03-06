/*
 * Copyright (c) 2016, Peter Abeles. All Rights Reserved.
 *
 * This file is part of DeepBoof
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package deepboof.forward;

import deepboof.Function;
import deepboof.factory.FactoryForwards;
import deepboof.tensors.Tensor_F32;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Abeles
 */
public abstract class ChecksForwardSpatialMaxPooling_F32 extends ChecksForwardSpatialWindow_F32<ConfigSpatial> {

	public ChecksForwardSpatialMaxPooling_F32() {
		numberOfConfigurations = 3;
	}

	@Override
	public Function<Tensor_F32> createForwards(int which) {
		config = new ConfigSpatial();
		configPadding = new ConfigPadding();

		config.WW = 3;
		config.HH = 4;

		if( which == 1 ) {
			config.WW = 1;
			config.HH = 1;
		} else if( which == 2 ) {
			configPadding.x0 = 1;
			configPadding.x1 = 2;
			configPadding.y0 = 3;
			configPadding.y1 = 4;
		} else if( which != 0  ){
			throw new RuntimeException("Unexpected");
		}

		return createForwards(config,configPadding);
	}

	protected abstract Function<Tensor_F32> createForwards( ConfigSpatial configSpatial ,
															ConfigPadding configPadding );

	@Override
	public SpatialPadding2D_F32 createPadding(int which ) {
		return (SpatialPadding2D_F32)FactoryForwards.spatialPadding(configPadding,Tensor_F32.class);
	}

	@Override
	protected void checkParameterShapes(int[] input, List<int[]> parameters) {
		assertEquals(0,parameters.size());
	}

	@Override
	public int inputToOutputChannelCount(int numInput) {
		return numInput;
	}

	@Override
	protected float[] computeExpected(Tensor_F32 input, List<Tensor_F32> parameters, int batch , int y, int x) {

		int C = input.length(1);

		float output[] = new float[C];
		for (int channel = 0; channel < C; channel++) {
			float max = -Float.MAX_VALUE;

			for (int i = 0; i < config.HH; i++) {
				for (int j = 0; j < config.WW; j++) {
					float v = input.get(batch,channel,y+i,x+j);
					if( v > max )
						max = v;
				}
			}

			output[channel] = max;
		}

		return output;
	}
}
