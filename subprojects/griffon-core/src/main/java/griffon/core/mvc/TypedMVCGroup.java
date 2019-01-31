/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.core.mvc;

import griffon.annotations.core.Nonnull;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonView;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public interface TypedMVCGroup<M extends GriffonModel, V extends GriffonView, C extends GriffonController> extends MVCGroup {
    @Nonnull
    M model();

    @Nonnull
    V view();

    @Nonnull
    C controller();

    @Nonnull
    MVCGroup delegate();
}
