/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Incorporated - initial API and implementation
 *******************************************************************************/

package com.redhat.fabric8analytics.lsp.eclipse.ui.internal;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.redhat.fabric8analytics.lsp.eclipse.core.RecommenderAPIException;
import com.redhat.fabric8analytics.lsp.eclipse.core.RecommenderAPIProvider;
import com.redhat.fabric8analytics.lsp.eclipse.ui.AnalysesViewer;

public class AnalysesJobHandler extends Job{

	private static final int TIMER_INTERVAL = 10000;

	private String jobId = null;

	private AnalysesViewer viewer;
	
	private RecommenderAPIProvider provider;
	
	private boolean viewerInitialized = false;

	public AnalysesJobHandler(String jobID, AnalysesViewer viewer, RecommenderAPIProvider provider) {
		super("Analyses check Job");
		this.jobId = jobID;
		this.viewer = viewer;
		this.provider = provider;
	}

	protected IStatus run(IProgressMonitor monitor) {
		if (!viewerInitialized) {
			URL url;
			try {
				url = new URL("platform:/plugin/com.redhat.fabric8analytics.lsp.eclipse.ui/templates/index.html");
				url = FileLocator.toFileURL(url);
				viewer.showAnalyses(url.toString());
			} catch (IOException e) {
				// do nothing, continue with analyses
			} finally {
				viewerInitialized = true;
			}
		}
		
		try {
			if (!provider.analysesFinished(jobId)) {
				schedule(TIMER_INTERVAL);	
			} else {
				viewer.showAnalyses(provider.getAnalysesURL(jobId));
			}
		} catch (RecommenderAPIException e) {
			MessageDialogUtils.displayErrorMessage("Error while running stack analyses", e);
		}
		return Status.OK_STATUS;
	}
}
