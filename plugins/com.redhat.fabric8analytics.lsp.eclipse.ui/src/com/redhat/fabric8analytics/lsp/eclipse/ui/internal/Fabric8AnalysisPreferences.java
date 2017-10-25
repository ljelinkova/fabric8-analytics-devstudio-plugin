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

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.lsp4e.LanguageServiceAccessor;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageServer;

import com.redhat.fabric8analytics.lsp.eclipse.ui.Fabric8AnalysisLSUIActivator;

public class Fabric8AnalysisPreferences {

	public static final String LSP_SERVER_ENABLED = "Fabric8AnalysisPreferences.LSP_SERVER_ENABLED";

	private static final Fabric8AnalysisPreferences INSTANCE = new Fabric8AnalysisPreferences();

	private Fabric8AnalysisPreferences() {
		Fabric8AnalysisLSUIActivator.getDefault().getPreferenceStore().setDefault(LSP_SERVER_ENABLED, true);

		IPreferenceStore preferenceStore = Fabric8AnalysisLSUIActivator.getDefault().getPreferenceStore();
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (Fabric8AnalysisPreferences.LSP_SERVER_ENABLED.equals(event.getProperty())) {
					IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
					IProject[] projects = workspaceRoot.getProjects();
					
					for (IProject project : projects) {
						System.out.println("*** project " + project.getName());
						Predicate<ServerCapabilities> predicate = new Predicate<ServerCapabilities>() {
							public boolean test(ServerCapabilities capability) {
								return true;
							};
						};

						if (isLSPServerEnabled()) {
						} else {
							List<LanguageServer> servers = LanguageServiceAccessor.getLanguageServers(project, predicate);
							System.out.println("Number of servers " + servers.size());
							for (LanguageServer server : servers) {
								System.out.println(server);
								server.shutdown();	
							}
						}
						
					}

				}
			}
		});
	}

	public static Fabric8AnalysisPreferences getInstance() {
		return INSTANCE;
	}

	public boolean isLSPServerEnabled() {
		return Fabric8AnalysisLSUIActivator.getDefault().getPreferenceStore().getBoolean(LSP_SERVER_ENABLED);
	}

	public void setLSPServerEnabled(boolean enabled) {
		Fabric8AnalysisLSUIActivator.getDefault().getPreferenceStore().setValue(LSP_SERVER_ENABLED, enabled);
	}
}
