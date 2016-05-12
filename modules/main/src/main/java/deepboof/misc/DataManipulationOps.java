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

package deepboof.misc;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import deepboof.tensors.Tensor_F32;

/**
 * @author Peter Abeles
 */
public class DataManipulationOps {

	public static void normalize( GrayF32 image , float mean , float stdev ) {
		for (int y = 0; y < image.height; y++) {
			int index = image.startIndex + y*image.stride;
			int end = index + image.width;
			while( index < end ) {
				image.data[index] = (image.data[index]-mean)/stdev;
				index++;
			}
		}
	}

	/**
	 * Converts an image into a spatial tensor
	 *
	 * @param input BoofCV planar image
	 * @param output Tensor
	 * @param miniBatch Which mini-batch in the tensor should the image be written to
	 */
	public static void imageToTensor(Planar<GrayF32> input , Tensor_F32 output , int miniBatch) {
		if( input.isSubimage())
			throw new RuntimeException("Subimages not accepted");
		if( output.getDimension() != 4 )
			throw new IllegalArgumentException("Output should be 4-DOF.  batch + spatial (channel,height,width)");
		if( output.length(1) != input.getNumBands() )
			throw new IllegalArgumentException("Number of bands don't match");
		if( output.length(2) != input.getHeight() )
			throw new IllegalArgumentException("Spatial height doesn't match");
		if( output.length(3) != input.getWidth() )
			throw new IllegalArgumentException("Spatial width doesn't match");

		for (int i = 0; i < input.getNumBands(); i++) {
			GrayF32 band = input.getBand(i);
			int indexOut = output.idx(miniBatch,i,0,0);

			int length = input.width*input.height;
			System.arraycopy(band.data, 0,output.d,indexOut,length);
		}
	}
}
