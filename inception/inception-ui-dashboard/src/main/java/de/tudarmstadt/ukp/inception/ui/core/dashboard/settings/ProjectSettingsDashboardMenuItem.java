/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.ui.core.dashboard.settings;

import org.apache.wicket.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.inception.ui.core.dashboard.config.DashboardAutoConfiguration;
import de.tudarmstadt.ukp.inception.ui.core.dashboard.settings.details.ProjectDetailPage;

/**
 * <p>
 * This class is exposed as a Spring Component via
 * {@link DashboardAutoConfiguration#projectSettingsDashboardMenuItem()}.
 * </p>
 */
@Order(8000)
public class ProjectSettingsDashboardMenuItem
    extends ProjectSettingsMenuItemBase
{
    private @Autowired UserDao userRepo;

    @Override
    public String getPath()
    {
        return "/settings";
    }

    @Override
    public IconType getIcon()
    {
        return FontAwesome5IconType.cogs_s;
    }

    @Override
    public String getLabel()
    {
        return "Settings";
    }

    @Override
    public Class<? extends Page> getPageClass()
    {
        return ProjectDetailPage.class;
    }

    @Override
    public boolean applies(Project aProject)
    {
        return super.applies(aProject) || userRepo.isAdministrator(userRepo.getCurrentUser());
    }
}
