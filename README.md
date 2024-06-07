paper-management
├── frontend/
│   ├── public/                    # 公共资源
│   │   ├── index.html             # 主HTML文件
│   │   ├── paper_classification.html
│   │   ├── paper_management.html
│   │   ├── paper_network.html
│   │   ├── paper_query.html
│   │   ├── paper_scorereference_management.html
│   │   ├── css/                   # 样式文件
│   │   │   └── styles.css         # 主样式文件
│   │   ├── js/                    # JavaScript 文件
│   │   │   └── app.js             # 主应用文件
│   ├── server.js                  # 用于启动前端服务器
│   ├── package.json               # 前端依赖配置（使用 npm）
├── backend/                       # 后端代码
│   ├── src/                       # 源代码
│   │   ├── main/
│   │   │   ├── java/              # Java 源文件
│   │   │   │   └── com/
│   │   │   │       └── myproject/
│   │   │   │           ├── PaperManagementApplication.java  # 主应用文件
│   │   │   │           ├── controller/                      # 控制器
│   │   │   │           │   ├── PaperController.java         # 处理论文相关请求
│   │   │   │           │   └── CategoryController.java      # 处理分类相关请求
│   │   │   │           ├── model/                           # 数据模型
│   │   │   │           │   ├── Paper.java                   # 论文实体类
│   │   │   │           │   └── Category.java                # 分类实体类
│   │   │   │           ├── repository/                      # 数据库操作
│   │   │   │           │   ├── PaperRepository.java         # 论文数据仓库
│   │   │   │           │   └── CategoryRepository.java      # 分类数据仓库
│   │   │   │           ├── service/                         # 服务层
│   │   │   │           │   ├── PaperService.java            # 处理论文业务逻辑
│   │   │   │           │   └── CategoryService.java         # 处理分类业务逻辑
│   │   │   │           └── specification/                   # 规格类
│   │   │   │               └── PaperSpecification.java      # 论文查询规格
│   │   ├── resources/
│   │   │   ├── application.properties                       # 配置文件
│   │   ├── static/                                          # 静态资源
│   │   └── templates/                                       # 模板文件
│   ├── pom.xml                                              # Maven 项目配置
├── paper-files/                                             # 用于存储上传的论文文件
│   ├── example-paper1.pdf
│   ├── example-paper2.pdf
│   └── ...                                                  # 更多上传的论文文件
├── database/                                                # 数据库相关文件
│   ├── migrations/                                          # 数据库迁移
│   │   └── categories.sql                                   # 分类表SQL文件
│   ├── seeds/                                               # 数据库种子数据
├── docs/                                                    # 文档
│   ├── README.md                                            # 项目说明
│   ├── API.md                                               # API 文档
│   ├── CONTRIBUTING.md                                      # 贡献指南
├── .gitignore                                               # Git忽略文件
├── LICENSE                                                  # 许可证
└── README.md                                                # 项目说明


程序使用使用 Node.js 20.14 和 Express 框架来构建后端服务，并使用 mySQL作为数据库。
前端程序使用css、html和JavaScript来构建。后端使用Java语言来编写。


