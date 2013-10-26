{
  open Lexing
}

rule main = parse
| "VALUES " { values lexbuf }
| _  { main lexbuf }

and values = parse
| ";" { main lexbuf }
| "(" (['0'-'9']+ as id) ",'" (([^'\''] | "\\'")* as lang) "','" (([^'\''] | "\\'")* as target) "')"","?
{ if (lang = "en") then print_endline (id ^ "\t" ^ target); values lexbuf }

{
  let lexbuf = from_channel stdin

  let () = main lexbuf
}
