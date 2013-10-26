{
  open Lexing
}

rule main = parse
| "<page>" { page "" lexbuf }
| _  { main lexbuf }

and page p = parse
| "</page>" { () }
| "<title>" { page (pagename lexbuf) lexbuf }
| "<ns>" { if num lexbuf = 0 then page p lexbuf else skip_page lexbuf }
| "<id>" { print_endline (string_of_int (num lexbuf) ^ "\t" ^ p); skip_page lexbuf }
| _ { page p lexbuf }

and pagename = parse
| [^'<' '"' '|' ']']* as t { t }

and num = parse
| ['0'-'9']+ as n { int_of_string n }

and skip_page = parse
| "</page>" { () }
| _ { skip_page lexbuf }

{
  let lexbuf = from_channel stdin

  let () = while true do main lexbuf done
}
