/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.restapi.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ActionMetadata", description = "Represents a Sponge action metadata")
public class RestActionMetadata {

    private String name;

    private String displayName;

    private List<RestActionArgMetadata> argsMetadata;

    public RestActionMetadata() {
        //
    }

    public RestActionMetadata(String name, String displayName, List<RestActionArgMetadata> argsMetadata) {
        this.name = name;
        this.displayName = displayName;
        this.argsMetadata = argsMetadata;
    }

    @ApiModelProperty(value = "The action name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The action display name", required = false)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @ApiModelProperty(value = "The action arguments metadata", required = false)
    public List<RestActionArgMetadata> getArgsMetadata() {
        return argsMetadata;
    }

    public void setArgsMetadata(List<RestActionArgMetadata> argsMetadata) {
        this.argsMetadata = argsMetadata;
    }
}
