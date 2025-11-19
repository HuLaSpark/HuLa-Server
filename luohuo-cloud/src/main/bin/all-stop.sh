#!/bin/bash

/bin/bash /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/restart-luohuo-gateway.sh stop
/bin/bash /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/restart-luohuo-oauth.sh stop
/bin/bash /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/restart-luohuo-base.sh stop
/bin/bash /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/restart-luohuo-system.sh stop
/bin/bash /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/restart-luohuo-ai.sh stop
/bin/bash /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/restart-luohuo-im.sh stop
/bin/bash /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/restart-luohuo-ws.sh stop