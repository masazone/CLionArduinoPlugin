/*
 * Copyright (c) 2015-2018 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

package io.github.francoiscambell.clionarduinoplugin.resources;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class ResourceUtils {
    public static String getResourceFileContent(Class clazz, String resourcePath) {
        StringWriter writer = new StringWriter();
        InputStream inputStream = clazz.getResourceAsStream(resourcePath);
        getStreamContent(writer, inputStream);
        return writer.toString();
    }

    public static void getStreamContent(final StringWriter writer, final InputStream inputStream) {
        try {
            IOUtils.copy(inputStream, writer, "UTF-8");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
