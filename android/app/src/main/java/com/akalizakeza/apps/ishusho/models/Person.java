/*
 * Copyright 2016 Google Inc. All Rights Reserved.
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

package com.akalizakeza.apps.ishusho.models;

import java.util.Map;

public class Person {
    private String full_name;
    private String profile_picture;
    private Map<String, Boolean> posts;
    private Map<String, Object> following;

    public Person() {

    }

    public Person(String full_name, String profile_picture) {
        this.full_name = full_name;
        this.profile_picture = profile_picture;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public Map<String, Boolean> getPosts() {
        return posts;
    }

    public Map<String, Object> getFollowing() {
        return following;
    }
}
