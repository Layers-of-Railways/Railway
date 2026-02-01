#  Steam 'n' Rails
#  Copyright (c) 2025 The Railways Team
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Lesser General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
#  GNU Lesser General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public License
#  along with this program. If not, see <https://www.gnu.org/licenses/>.

import os
import json

def sort_file(lang: str):
    with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "r") as f:
        strings = json.load(f)

    new_strings = {k: strings[k] for k in sorted(strings.keys())}

    with open(f"../common/src/main/resources/assets/railways/lang/{lang}.json", "w") as f:
        json.dump(new_strings, f, indent=2, ensure_ascii=False)

if __name__ == "__main__":
    languages = ["de_at", "de_de", "es_es", "fr_fr", "ja_jp", "ko_kr", "nl_nl", "pl_pl", "zh_cn", "da_dk", "is_is",
                 "no_no", "sv_se", "ru_ru", "uk_ua", "en_gb", "tr_tr", "ro_ro", "th_th", "es_mx"]

    for language in languages:
        if os.path.exists(f"../common/src/main/resources/assets/railways/lang/{language}.json"):
            print(f"Sorting {language} translations...")
            sort_file(language)
        else:
            print(f"Language file for {language} does not exist, skipping.")