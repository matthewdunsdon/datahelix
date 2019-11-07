#!/usr/bin/env bash

files=`find . -type f -name '*.java' | xargs grep -L "Licensed under the Apache License"`
# echo {files}

if [[ -z ${files} ]]
then
	echo "No files missing headers."
	exit 0
else
	echo "Consider running header-update.sh from the root to update these files automatically!"
	echo "Some files are missing headers:"
fi

for i in ${files}; do
  echo ${i}
done

exit 1
