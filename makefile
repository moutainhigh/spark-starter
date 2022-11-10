clean:
	mvn clean

install:clean
	./bin/install.sh

deploy:clean
	./bin/deploy.sh

checkout:
	./bin/checkout.sh {分支名}
