package com.railwayteam.railways.content.buffer.forge;

import com.railwayteam.railways.content.buffer.TrackBufferBlock;

public class TrackBufferBlockImpl extends TrackBufferBlock {
	protected TrackBufferBlockImpl(Properties pProperties) {
		super(pProperties);
	}

	public static TrackBufferBlock create(Properties properties) {
		return new TrackBufferBlockImpl(properties);
	}
}
