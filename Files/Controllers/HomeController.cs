using ChatFileManagement.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using Microsoft.Net.Http.Headers;
using System.Threading.Tasks;
using CSRedis;

namespace ChatFileManagement.Controllers
{
    public class FileController : Controller
    {
        private readonly ILogger<FileController> _logger;
        private RedisClient _client;
        public FileController(ILogger<FileController> logger)
        {
            _logger = logger;
            _client = new RedisClient("172.16.238.104", 6379);
        }
        [HttpGet]
        [Route("/")]
        public IActionResult resp()
        {
            return new JsonResult(new { status = "Working" });
        }
        [HttpGet]
        [Route("/del/{name}")]
        public IActionResult del(String name)
        {
            System.IO.File.Delete("/home/files/" + name + _client.Get(name));
            return new JsonResult(new { status = "Deleted" });
        }
        [HttpGet]
        [Route("file/return/{chat}/{name}")]
        public IActionResult FileResp(String chat, String name)
        {
            try
            {
                return new FileStreamResult(System.IO.File.OpenRead("/home/files/" + name + _client.Get(name)), new MediaTypeHeaderValue("application/octet-stream"));
            }
            catch(Exception ex)
            {
                return new JsonResult(new { data="File does not exist"});
            }
        }
        [HttpPost]
        [Route("file/create/{chat}/{name}/{type}")]
        public IActionResult FileCr(String name, String type)
        {
            long i = 0;
            while (_client.Get(name + "_" + i) != null)
            {
                i++;
            }
            _client.Set(name + "_" + i, "." + type);
            name += "_" + i;
            try
            {
                Stream s = HttpContext.Request.Body;
                using (var ms = new MemoryStream())
                {
                    s.CopyTo(ms);
                    System.IO.File.WriteAllBytes("/home/files/" + name + "." + type, ms.ToArray());
                }
            }
            finally
            {
            }
            return new JsonResult(new { name = name });

        }
    }
}
