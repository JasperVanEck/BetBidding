/*
 * Copyright (c) 2012, Isaiah van der Elst (isaiah.v@comcast.net)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.gearman.impl.serverpool;

import org.gearman.GearmanJobStatus;

public class GearmanJobStatusImpl implements GearmanJobStatus {
	
	public static final GearmanJobStatus NOT_KNOWN = new GearmanJobStatusImpl(false, false, 0, 0);
	
	private final boolean isKnown;
	private final boolean isRunning;
	private final long numerator;
	private final long denominator;
	
	GearmanJobStatusImpl(boolean isKnown, boolean isRunning, long num, long den) {
		this.isKnown = isKnown;
		this.isRunning = isRunning;
		this.numerator = num;
		this.denominator = den;
	}
	
	@Override
	public boolean isKnown() {
		return isKnown;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public long getNumerator() {
		return numerator;
	}

	@Override
	public long getDenominator() {
		return denominator;
	}

}
