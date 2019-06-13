echo "$(crontab -l)
@reboot java -jar $PWD/intraproxy.jar" | crontab -
