/*
 * Copyright (c) 2015-2016 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.clionarduinoplugin.components;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.settings.ApplicationSettingsForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class ApplicationConfigurable implements SearchableConfigurable {
    @Nullable private ApplicationSettingsForm myForm = null;
    @NotNull final private ArduinoApplicationSettings mySettings;

    private ApplicationConfigurable() {
        this.mySettings = ArduinoApplicationSettings.getInstance().getState();
    }

    @NotNull
    @Override
    public String getId() {
        return "com.vladsch.clionarduinoplugin.components.ApplicationConfigurable";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        //final ProjectSettingsForm form = getForm();
        //final Runnable runnable = form.enableSearch(option);
        //if (runnable != null) {
        //    return new Runnable() {
        //        @Override
        //        public void run() {
        //            form.getComponent().show();
        //            runnable.run();
        //        }
        //    };
        //}
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return Bundle.message("plugin.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null; //"com.vladsch.markdown.navigator.settings.stylesheet";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return getForm().getComponent();
    }

    @NotNull
    public ApplicationSettingsForm getForm() {
        if (myForm == null) {
            myForm = new ApplicationSettingsForm(mySettings);
        }
        return myForm;
    }

    @Override
    public boolean isModified() {
        return getForm().isModified(mySettings);
    }

    @Override
    public void apply() throws ConfigurationException {
        getForm().apply(mySettings);
    }

    @Override
    public void reset() {
        getForm().reset(mySettings);
    }

    @Override
    public void disposeUIResources() {
        if (myForm != null) {
            Disposer.dispose(myForm);
            myForm = null;
        }
    }
}
